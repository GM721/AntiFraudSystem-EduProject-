package antifraud.Controllers;

import antifraud.DB.Entities.User;
import antifraud.DB.Repositories.UserRepository;
import antifraud.RequestAndResponse.ChangeRoleRequest;
import antifraud.RequestAndResponse.LockRequest;
import antifraud.RequestAndResponse.UserForRequest;
import antifraud.Enums.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @PostMapping(value = "/api/auth/user")
    public ResponseEntity authUser(@RequestBody User user) {
        try {
            if (!userRepository.existsByUsername(user.getUsername())) {
                if(!userRepository.findAll().iterator().hasNext()){
                    user.setRole(Roles.ADMINISTRATOR.getRole());
                    user.setLock(false);
                } else {
                    user.setRole(Roles.MERCHANT.getRole());
                    user.setLock(true);
                }
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(new UserForRequest(userRepository.findUserByUsername(user.getUsername()).get()));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @PutMapping("/api/auth/role")
    public ResponseEntity changeRole (@RequestBody ChangeRoleRequest request) {
        try {
            if(userRepository.existsByUsername(request.getUsername())) {
                User user = userRepository.findUserByUsername(request.getUsername()).get();
                if(request.getRole() !=  Roles.ADMINISTRATOR) {
                    if(!request.getRole().getRole().equals(user.getRole())) {
                        userRepository.updateRole(request.getRole().getRole(),request.getUsername());
                        user.setRole(request.getRole().getRole());
                        return ResponseEntity.status(HttpStatus.OK).body(new UserForRequest(user));
                    } else {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/api/auth/access")
    public  ResponseEntity changeAccess (@RequestBody LockRequest request) {
        try {
            if(userRepository.findUserByUsername(request.getUsername()).get().getRole() == Roles.ADMINISTRATOR.getRole() || !userRepository.existsByUsername(request.getUsername())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            switch (request.getOperation()) {
                case ("LOCK") :
                    userRepository.updateLockStatus(true,request.getUsername());
                    return ResponseEntity.status(HttpStatus.OK).body(Map.of("status" , "User " + request.getUsername() + " locked!"));
                case ("UNLOCK"):
                    userRepository.updateLockStatus(false,request.getUsername());
                    return ResponseEntity.status(HttpStatus.OK).body(Map.of("status" , "User " + request.getUsername() + " unlocked!"));
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @GetMapping("/api/auth/list")
    public ResponseEntity getUsersList() {
        try {
            List<UserForRequest> userList = new ArrayList<>();
            for(User user: userRepository.findAllByOrderByIdAsc()){
                userList.add(new UserForRequest(user));
            }
            return ResponseEntity.status(HttpStatus.OK).body(userList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity deleteUser(@PathVariable String username) {
        if(userRepository.existsByUsername(username)){
            userRepository.removeByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("username", username, "status", "Deleted successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

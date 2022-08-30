package antifraud.RequestAndResponse;

import antifraud.DB.Entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForRequest {
    private Long id;

    private String name;

    private String username;

    private String role;

    public UserForRequest(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}

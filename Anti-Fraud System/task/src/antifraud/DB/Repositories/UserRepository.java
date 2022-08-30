package antifraud.DB.Repositories;


import antifraud.DB.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository <User, Long>{

    @Transactional
    Optional<User> findUserByUsername(String username);
    boolean existsByUsername(String username);
    @Transactional
    void removeByUsername(String username);

    List<User> findAllByOrderByIdAsc();
    @Modifying
    @Transactional
    @Query("UPDATE user u SET u.role = :role WHERE u.username = :username")
    int updateRole(@Param("role") String role, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE user u SET u.isLock = :isLock WHERE u.username = :username")
    void updateLockStatus(@Param("isLock") boolean isLock, @Param("username") String username);
}

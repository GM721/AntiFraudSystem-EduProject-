package antifraud.DB.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false,unique = true)
    private String username;


    @Column(nullable = false)
    private  String password;

    @Column
    private String role;

    @Column
    private boolean isLock;
}

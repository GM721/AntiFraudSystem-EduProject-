package antifraud.DB.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
@Getter
@Setter
public class SuspiciousIp {

    @Id
    @GeneratedValue
    Long id;

    @Column(unique = true)
    String ip;

}

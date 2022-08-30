package antifraud.DB.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Getter
@Setter
public class StolenCard {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column
    private String number;

}

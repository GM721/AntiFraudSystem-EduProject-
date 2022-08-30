package antifraud.DB.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class CardLimit {

    @Id
    private String number;

    private int allowLimit = 200;

    private int manualLimit = 1500;

    public CardLimit setCardNumber(String number) {
        this.number = number;
        return this;
    }
}

package antifraud.DB.Entities;

import antifraud.Enums.Regions;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue
    Long transactionId;

    Long amount;

    String ip;

    String number;

    String region;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date date;

    String result;

    String feedback = "";



}

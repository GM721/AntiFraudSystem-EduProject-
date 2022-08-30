package antifraud.RequestAndResponse;

import antifraud.Enums.TransStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionFeedback {

    Long transactionId;

    TransStatus feedback;

}

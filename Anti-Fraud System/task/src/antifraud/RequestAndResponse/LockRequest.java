package antifraud.RequestAndResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LockRequest {

    private String username;

    private String operation;

}

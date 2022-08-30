package antifraud.RequestAndResponse;

import antifraud.Enums.Roles;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRoleRequest {
    private String username;
    private Roles role;

}

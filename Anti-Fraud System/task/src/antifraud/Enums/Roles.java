package antifraud.Enums;

public enum Roles {
    MERCHANT("MERCHANT"),
    ADMINISTRATOR("ADMINISTRATOR"),
    SUPPORT("SUPPORT");
    private final String role;
    Roles (String role) {
        this.role = role;
    }

    public String getRole(){
        return this.role;
    }

}

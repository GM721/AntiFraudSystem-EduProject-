package antifraud.Enums;

public enum TransStatus {
    ALLOWED ("ALLOWED"),
    MANUAL_PROCESSING ("MANUAL_PROCESSING"),
    PROHIBITED ("PROHIBITED");

    private final String transStatus;

    TransStatus (String role) {
        this.transStatus = role;
    }

    public String getTransStatus(){
        return this.transStatus;
    }

    public static boolean checkTransStatus (String transStatus) throws Exception {
        for (TransStatus status : TransStatus.values()) {
            if(status.getTransStatus().equals(transStatus)) {
                return true;
            }
        }
        return false;
    }
}

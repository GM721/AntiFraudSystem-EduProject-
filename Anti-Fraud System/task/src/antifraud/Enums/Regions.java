package antifraud.Enums;

public enum Regions {

    EAP("EAP"),
    ECA("ECA"),
    HIC("HIC"),
    LAC("LAC"),
    MENA("MENA"),
    SA("SA"),
    SSA("SSA");

    private final String region;
    Regions (String region) {
        this.region = region;
    }

    public String getRegion(){
        return this.region;
    }

    public static boolean checkRegion (String region) throws Exception {
        for (Regions regions : Regions.values()) {
            if(regions.getRegion().equals(region)) {
                return true;
            }
        }
        return false;
    }
}

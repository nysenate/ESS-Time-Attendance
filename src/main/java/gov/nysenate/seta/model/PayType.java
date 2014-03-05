package gov.nysenate.seta.model;

public enum PayType
{
    RA("Regular Annual", 1820, true),
    SA("Special Annual", 0, true),
    TE("Temporary", 0, false);

    private String desc;
    private int minHours;
    private boolean biweekly;

    private PayType(String desc, int minHours, boolean biweekly) {
        this.desc = desc;
        this.minHours = minHours;
        this.biweekly = biweekly;
    }

    public String getDesc() {
        return desc;
    }

    public int getMinHours() {
        return minHours;
    }

    public boolean isBiweekly() {
        return biweekly;
    }
}

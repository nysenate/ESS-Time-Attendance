package gov.nysenate.seta.model;

public enum PayType
{
    RA("Regular Annual"),
    SA("Special Annual"),
    TE("Temporary");

    private String desc;

    PayType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

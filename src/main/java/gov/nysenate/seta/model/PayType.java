package gov.nysenate.seta.model;

public enum PayType
{
    RA("Regular Annual"),
    SA("Special Annual"),
    TE("Temporary");

    String desc;
    PayType(String desc) {
        this.desc = desc;
    }
}

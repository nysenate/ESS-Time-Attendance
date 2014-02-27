package gov.nysenate.seta.model;

public enum Gender
{
    M("Male"),
    F("Female");

    String desc;

    Gender(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

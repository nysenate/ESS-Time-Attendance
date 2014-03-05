package gov.nysenate.seta.model;

public enum TimeRecordStatus
{
    SUBMITTED("S", "Submitted", "S", 2);

    protected String code;
    protected String name;
    protected String unlockedFor;
    protected int orderLevel;

    private TimeRecordStatus(String code, String name, String unlockedFor, int orderLevel) {
        this.code = code;
        this.name = name;
        this.unlockedFor = unlockedFor;
        this.orderLevel = orderLevel;
    }
}

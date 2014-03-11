package gov.nysenate.seta.model;

public enum TimeRecordStatus
{
    SUBMITTED("S", "Submitted", "S"),
    NOT_SUBMITTED("NS","Not Submitted","E"),
    APPROVED("A","Approved by Supervisor", "P"),
    DISAPPROVED("D","DisApproved by Supervisor","E"),
    SUBMITTED_PERSONNEL("SP","Submitted to Personnel","P"),
    APPROVED_PERSONNEL("AP","Approved by Personnel","P"),
    DISAPPROVED_PERSONNEL("DP","DisApproved by Personnel","E");

    protected String code;
    protected String name;
    protected String unlockedFor;
    protected int orderLevel;

    private TimeRecordStatus(String code, String name, String unlockedFor) {
        this.code = code;
        this.name = name;
        this.unlockedFor = unlockedFor;
    }

    public static TimeRecordStatus valueOfCode(String code){

        for (TimeRecordStatus status : TimeRecordStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;

    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getUnlockedFor() {
        return unlockedFor;
    }
}

package gov.nysenate.seta.model.attendance;

/**
 * The TimeRecordStatus enum represents the possible states that a time record can be in.
 */
public enum TimeRecordStatus
{
    SUBMITTED("S", "Submitted", "S"),
    NOT_SUBMITTED("NS","Not Submitted","E"),
    APPROVED("A","Approved by Supervisor", "P"),
    DISAPPROVED("D","Disapproved by Supervisor","E"),
    SUBMITTED_PERSONNEL("SP","Submitted to Personnel","P"),
    APPROVED_PERSONNEL("AP","Approved by Personnel","P"),
    DISAPPROVED_PERSONNEL("DP","Disapproved by Personnel","E");

    protected String code;
    protected String name;

    /** The unlockedFor string indicates who can perform an action on the time record at that given stage.
     *  For example when the status is 'Submitted' the supervisor 'S' can take action (i.e approve/disapprove).
     *  Possible values: 'E' = Employee, 'S' = Supervisor, 'P' = Personnel */
    protected String unlockedFor;

    private TimeRecordStatus(String code, String name, String unlockedFor) {
        this.code = code;
        this.name = name;
        this.unlockedFor = unlockedFor;
    }

    public static TimeRecordStatus valueOfCode(String code){
        for (TimeRecordStatus status : TimeRecordStatus.values()) {
            if (status.code.equals(code))
            return  status;
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

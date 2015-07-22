package gov.nysenate.seta.model.attendance;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import java.util.Set;

/**
 * The TimeRecordStatus enum represents the possible states that a time record can be in.
 */
public enum TimeRecordStatus
{
    SUBMITTED("S", "Submitted", "S"),
    NOT_SUBMITTED("W","Not Submitted","E"),
    APPROVED("A","Approved by Supervisor", "P"),
    DISAPPROVED("D","Disapproved by Supervisor","E"),
    SUBMITTED_PERSONNEL("SP","Submitted to Personnel","P"),
    APPROVED_PERSONNEL("AP","Approved by Personnel","P"),
    DISAPPROVED_PERSONNEL("DP","Disapproved by Personnel","E"), ;

    protected String code;
    protected String name;

    /** The unlockedFor string indicates who can perform an action on the time record at that given stage.
     *  For example when the status is 'Submitted' the supervisor 'S' can take action (i.e approve/disapprove).
     *  Possible values: 'E' = Employee, 'S' = Supervisor, 'P' = Personnel */
    protected String unlockedFor;

    /** Mapping of unlockedFor values (E,S,P) to a set of corresponding time record statuses. */
    private static SetMultimap<String, TimeRecordStatus> unlockedForMap = TreeMultimap.create();
    static {
        for (TimeRecordStatus trs : TimeRecordStatus.values()) {
            unlockedForMap.put(trs.unlockedFor, trs);
        }
    }

    private TimeRecordStatus(String code, String name, String unlockedFor) {
        this.code = code;
        this.name = name;
        this.unlockedFor = unlockedFor;
    }

    public static TimeRecordStatus valueOfCode(String code){
        for (TimeRecordStatus status : TimeRecordStatus.values()) {
            if (status.code.equals(code))
            return status;
        }
        return null;
    }

    public boolean isUnlockedForEmployee() {
        return unlockedFor.equals("E");
    }

    public boolean isUnlockedForSupervisor() {
        return unlockedFor.equals("S");
    }

    public boolean isUnlockedForPersonnel() {
        return unlockedFor.equals("P");
    }

    public static Set<TimeRecordStatus> unlockedForEmployee() {
        return unlockedForMap.get("E");
    }

    public static Set<TimeRecordStatus> unlockedForSupervisor() {
        return unlockedForMap.get("S");
    }

    public static Set<TimeRecordStatus> unlockedForPersonnel() {
        return unlockedForMap.get("P");
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

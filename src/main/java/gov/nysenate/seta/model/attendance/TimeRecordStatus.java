package gov.nysenate.seta.model.attendance;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import java.util.Set;

import static gov.nysenate.seta.model.attendance.TimeRecordScope.*;

/**
 * The TimeRecordStatus enum represents the possible states that a time record can be in.
 */
public enum TimeRecordStatus
{
    SUBMITTED("S", "Submitted", SUPERVISOR),
    NOT_SUBMITTED("W","Not Submitted", EMPLOYEE),
    APPROVED("A","Approved by Supervisor", PERSONNEL),
    DISAPPROVED("D","Disapproved by Supervisor", EMPLOYEE),
    SUBMITTED_PERSONNEL("SP","Submitted to Personnel", PERSONNEL),
    APPROVED_PERSONNEL("AP","Approved by Personnel", PERSONNEL),
    DISAPPROVED_PERSONNEL("DP","Disapproved by Personnel", EMPLOYEE), ;

    protected String code;
    protected String name;

    /** The scope indicates who can perform an action on the time record at that given stage.
     *  For example when the status is 'Submitted' the supervisor scope 'S' can only take action (i.e approve/disapprove).
     */
    protected TimeRecordScope scope;

    /** Mapping of unlockedFor values (E,S,P) to a set of corresponding time record statuses. */
    private static SetMultimap<TimeRecordScope, TimeRecordStatus> unlockedForMap = TreeMultimap.create();
    static {
        for (TimeRecordStatus trs : TimeRecordStatus.values()) {
            unlockedForMap.put(trs.scope, trs);
        }
    }

    private TimeRecordStatus(String code, String name, TimeRecordScope scope) {
        this.code = code;
        this.name = name;
        this.scope = scope;
    }

    public static TimeRecordStatus valueOfCode(String code){
        for (TimeRecordStatus status : TimeRecordStatus.values()) {
            if (status.code.equals(code))
            return status;
        }
        return null;
    }

    public boolean isUnlockedForEmployee() {
        return scope.equals(EMPLOYEE);
    }

    public boolean isUnlockedForSupervisor() {
        return scope.equals(SUPERVISOR);
    }

    public boolean isUnlockedForPersonnel() {
        return scope.equals(PERSONNEL);
    }

    public static Set<TimeRecordStatus> unlockedForEmployee() {
        return unlockedForMap.get(EMPLOYEE);
    }

    public static Set<TimeRecordStatus> unlockedForSupervisor() {
        return unlockedForMap.get(SUPERVISOR);
    }

    public static Set<TimeRecordStatus> unlockedForPersonnel() {
        return unlockedForMap.get(PERSONNEL);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public TimeRecordScope getScope() {
        return scope;
    }
}
package gov.nysenate.seta.model.attendance;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    DISAPPROVED_PERSONNEL("DP","Disapproved by Personnel", EMPLOYEE),
    ;

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

    private static final EnumSet<TimeRecordStatus> inProgress =
        EnumSet.of(SUBMITTED, NOT_SUBMITTED, APPROVED, DISAPPROVED, SUBMITTED_PERSONNEL, DISAPPROVED_PERSONNEL);

    TimeRecordStatus(String code, String name, TimeRecordScope scope) {
        this.code = code;
        this.name = name;
        this.scope = scope;
    }

    public static TimeRecordStatus valueOfCode(String code){
        for (TimeRecordStatus status : TimeRecordStatus.values()) {
            if (status.code.equals(code)) return status;
        }
        return null;
    }

    public boolean isUnlockedForEmployee() {
        return EMPLOYEE.equals(scope);
    }

    public boolean isUnlockedForSupervisor() {
        return SUPERVISOR.equals(scope);
    }

    public boolean isUnlockedForPersonnel() {
        return PERSONNEL.equals(scope);
    }

    public static Set<TimeRecordStatus> getAll() {
        return new HashSet<>(unlockedForMap.values());
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

    public static Set<TimeRecordStatus> inProgress() {
        return inProgress;
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
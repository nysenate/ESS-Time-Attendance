package gov.nysenate.seta.model.attendance;

import java.util.Set;

public enum TimeRecordScope
{
    EMPLOYEE("E", TimeRecordStatus.unlockedForEmployee()),
    SUPERVISOR("S", TimeRecordStatus.unlockedForSupervisor()),
    PERSONNEL("P", TimeRecordStatus.unlockedForPersonnel());

    private String code;
    private Set<TimeRecordStatus> statuses;

    TimeRecordScope(String code, Set<TimeRecordStatus> statuses) {
        this.code = code;
        this.statuses = statuses;
    }

    public String getCode() {
        return code;
    }

    public Set<TimeRecordStatus> getStatuses() {
        return statuses;
    }
}
package gov.nysenate.seta.model.attendance;

import java.util.Set;
import java.util.function.Supplier;

public enum TimeRecordScope
{
    EMPLOYEE("E", TimeRecordStatus::unlockedForEmployee),
    SUPERVISOR("S", TimeRecordStatus::unlockedForSupervisor),
    PERSONNEL("P", TimeRecordStatus::unlockedForPersonnel)
    ;

    private String code;

    // A supplier is used since the scope lookup map in TimeRecordStatus
    // is uninitialized when this class is initialized
    private Supplier<Set<TimeRecordStatus>> statusSupplier;

    TimeRecordScope(String code, Supplier<Set<TimeRecordStatus>> statusSupplier) {
        this.code = code;
    }

    public Set<TimeRecordStatus> getStatuses() {
        return statusSupplier.get();
    }

    public String getCode() {
        return code;
    }
}
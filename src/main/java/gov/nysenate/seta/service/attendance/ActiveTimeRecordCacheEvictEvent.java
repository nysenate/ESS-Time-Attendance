package gov.nysenate.seta.service.attendance;

import gov.nysenate.seta.service.base.BaseEvent;

import java.time.LocalDateTime;
import java.util.Set;

public class ActiveTimeRecordCacheEvictEvent extends BaseEvent
{
    protected boolean allRecords;
    protected Set<Integer> affectedEmployees;

    public ActiveTimeRecordCacheEvictEvent(boolean allRecords, Set<Integer> affectedEmployees) {
        this.allRecords = allRecords;
        this.affectedEmployees = affectedEmployees;
    }

    public ActiveTimeRecordCacheEvictEvent(LocalDateTime createdDateTime, boolean allRecords, Set<Integer> affectedEmployees) {
        super(createdDateTime);
        this.allRecords = allRecords;
        this.affectedEmployees = affectedEmployees;
    }

    public boolean isAllRecords() {
        return allRecords;
    }

    public Set<Integer> getAffectedEmployees() {
        return affectedEmployees;
    }
}
package gov.nysenate.seta.service.attendance.validators;

import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.service.attendance.InvalidTimeRecordException;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static gov.nysenate.seta.model.attendance.TimeRecordStatus.*;

/**
 * Checks to ensure that a posted time record is following a valid progression through the time record life cycle
 * as indicated by changes in time record status
 */
@Service
public class LifeCycleTRV implements TimeRecordValidator {

    /**
     * Applicable for all posted time records
     */
    @Override
    public boolean isApplicable(TimeRecord record, TimeRecord previousState) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void checkTimeRecord(TimeRecord record, TimeRecord previousState) throws InvalidTimeRecordException {
        TimeRecordStatus newStatus = record.getRecordStatus();
        TimeRecordStatus prevStatus = previousState.getRecordStatus();
        // Get valid statuses that occur after previous status and ensure that the new status is contained in this set
        Set<TimeRecordStatus> validStatuses = getValidStatuses(prevStatus);
        if (!validStatuses.contains(newStatus)) {
            throw new InvalidTimeRecordException(InvalidTimeRecordCode.INVALID_STATUS_CHANGE,
                    String.format("Cannot post a %s time record when the previous status is %s", newStatus, prevStatus));
        }
    }

    /** --- Internal Methods --- */

    /**
     * Get a set of statuses that can follow the previous status
     */
    private Set<TimeRecordStatus> getValidStatuses(TimeRecordStatus prevStatus) {
        switch (prevStatus) {
            case NOT_SUBMITTED:
                return newHashSet(NOT_SUBMITTED, SUBMITTED);
            case SUBMITTED:
                return newHashSet(DISAPPROVED, APPROVED);
            case DISAPPROVED:
                return newHashSet(DISAPPROVED, SUBMITTED);
            case APPROVED:
            case SUBMITTED_PERSONNEL:
            case APPROVED_PERSONNEL:
                return newHashSet(DISAPPROVED_PERSONNEL, APPROVED_PERSONNEL);
            case DISAPPROVED_PERSONNEL:
                return newHashSet(DISAPPROVED_PERSONNEL, SUBMITTED_PERSONNEL);
            default:
                throw new IllegalArgumentException("previous time record status canoot be " + prevStatus + "!");
        }
    }
}

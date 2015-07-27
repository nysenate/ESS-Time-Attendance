package gov.nysenate.seta.service.attendance.validators;

import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.service.attendance.InvalidTimeRecordException;

import java.util.Set;

/**
 * An interface for a class that performs time record validation according to a specific time record rule
 */
public interface TimeRecordValidator {

    /**
     * Tests to see if this validation rule applies to the given time record and previous record state
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @return boolean - true iff the rule can be applied
     */
    boolean isApplicable(TimeRecord record, TimeRecord previousState);

    /**
     * Performs a check on a time record, throwing an exception if the time record is found to be invalid
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws InvalidTimeRecordException if the provided time record contains erroneous data
     */
    void checkTimeRecord(TimeRecord record, TimeRecord previousState) throws InvalidTimeRecordException;

}

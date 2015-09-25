package gov.nysenate.seta.service.attendance;

import gov.nysenate.common.WorkInProgress;
import gov.nysenate.seta.model.attendance.TimeRecord;

import java.util.Collection;
import java.util.List;

/**
 * A service that is responsible for generating time records and ensuring that active time records contain valid data
 */
@WorkInProgress(author = "Sam", since = "2015/09/15", desc = "building and testing time record generation methods")
public interface TimeRecordManager {

    /**
     * Ensure that the given employee has records that cover the given pay periods
     *  ensures that all records covering the pay periods contain correct and up to date employee information
     * @param empId int - employee id
     * @return int - the number of records created/modified
     */
    public int ensureRecords(int empId);

    /**
     * Ensure that all active employees have up to date, correct records for all active pay periods in the current year
     * @see #ensureRecords(int)
     */
    public void ensureAllActiveRecords();

    /**
     * A method that will generate and return unregistered time records for any ranges in the employee's open pay periods
     *  that are not covered by the given existing time records
     * This method is used for temporary employees who do not necessarily need a time record for all employed pay periods
     *   but need to have records available to fill out upon request
     * @param empId int - employee id
     * @param existingRecords Collection<TimeRecord> - time records
     * @return List<TimeRecords> - the newly generated unregistered time records
     */
    public List<TimeRecord> getUnusedRecords(int empId, Collection<TimeRecord> existingRecords);
}

package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TimeRecordDao extends BaseDao
{
    /** --- Retrieval methods --- */

    /**
     * @see #getRecordsDuring(int, java.time.LocalDate, java.time.LocalDate, java.util.Set)
     * Use this overload if you don't want to filter by statuses.
     */
    List<TimeRecord> getRecordsDuring(int empId, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves a list of time records for a given employee during a time range, ordered by earliest first.
     * @param empId int - Employee id
     * @param startDate Date - The start date
     * @param endDate Date - The end date
     * @param statuses Set<TimeRecordStatus> - The set of statuses to filter by
     * @return List<TimeRecord>
     */
    List<TimeRecord> getRecordsDuring(int empId, LocalDate startDate, LocalDate endDate, Set<TimeRecordStatus> statuses);

    /**
     * @see #getRecordsDuring(java.util.List, java.time.LocalDate, java.time.LocalDate, java.util.Set)
     * Use this overload if you don't want to filter by statuses.
     */
    Map<Integer, List<TimeRecord>> getRecordsDuring(List<Integer> empIds, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves a list of time records for the employees in the given list during a time range.
     * @param empIds List<Integer> - List of employee ids
     * @param startDate Date - The start date
     * @param endDate Date - The end date
     * @param statuses Set<TimeRecordStatus> - The set of statuses to filter by
     * @return Map<Integer, List<TimeRecord>>
     */
    Map<Integer, List<TimeRecord>> getRecordsDuring(List<Integer> empIds, LocalDate startDate, LocalDate endDate, Set<TimeRecordStatus> statuses);

    /** --- Insert/Update methods --- */

    /**
     * Update TimeRecord to the Timesheet Table
     * @param record - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    boolean saveRecord(TimeRecord record);
}

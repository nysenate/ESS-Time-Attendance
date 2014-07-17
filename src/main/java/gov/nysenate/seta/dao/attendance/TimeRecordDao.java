package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TimeRecordDao extends BaseDao
{
    /** --- Retrieval methods --- */

    /**
     * @see #getRecordsDuring(int, java.util.Date, java.util.Date, java.util.Set)
     * Use this overload if you don't want to filter by statuses.
     */
    public List<TimeRecord> getRecordsDuring(int empId, Date startDate, Date endDate);

    /**
     * Retrieves a list of time records for a given employee during a time range, ordered by earliest first.
     * @param empId int - Employee id
     * @param startDate Date - The start date
     * @param endDate Date - The end date
     * @param statuses Set<TimeRecordStatus> - The set of statuses to filter by
     * @return List<TimeRecord>
     */
    public List<TimeRecord> getRecordsDuring(int empId, Date startDate, Date endDate, Set<TimeRecordStatus> statuses);

    /**
     * @see #getRecordsDuring(java.util.List, java.util.Date, java.util.Date, java.util.Set)
     * Use this overload if you don't want to filter by statuses.
     */
    public Map<Integer, List<TimeRecord>> getRecordsDuring(List<Integer> empIds, Date startDate, Date endDate);

    /**
     * Retrieves a list of time records for the employees in the given list during a time range.
     * @param empIds List<Integer> - List of employee ids
     * @param startDate Date - The start date
     * @param endDate Date - The end date
     * @param statuses Set<TimeRecordStatus> - The set of statuses to filter by
     * @return Map<Integer, List<TimeRecord>>
     */
    public Map<Integer, List<TimeRecord>> getRecordsDuring(List<Integer> empIds, Date startDate, Date endDate, Set<TimeRecordStatus> statuses);

    /** --- Insert/Update methods --- */

    /**
     * Update TimeRecord to the Timesheet Table
     * @param record - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    public boolean saveRecord(TimeRecord record);
}

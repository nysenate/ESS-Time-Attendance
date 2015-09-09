package gov.nysenate.seta.service.attendance;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.exception.SupervisorException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TimeRecordService
{
    /**
     * Gets the distinct years that an employee has at least one time record for.
     * @param empId Integer - employee id
     * @param yearOrder - SortOrder - order the returned years
     * @return List<Integer>
     */
    List<Integer> getTimeRecordYears(Integer empId, SortOrder yearOrder);

    /**
     * Get time records for one or more employees, matching certain time record statuses, over a specified date range.
     * Will create time records for uncovered pay periods if desired,
     *
     * @param empIds Set<Integer> - employee ids
     * @param dateRange Range<LocalDate> - interval to check for
     * @param statuses Set<TimeRecordStatus> - time record statuses to retrieve
     * @param fillMissingRecords boolean - will create new records to fill pay period gaps if true
     * @return
     */
    List<TimeRecord> getTimeRecords(Set<Integer> empIds, Range<LocalDate> dateRange,
                                    Set<TimeRecordStatus> statuses,
                                    boolean fillMissingRecords);

    /**
     * Retrieves the time records for an employee with employee id 'empId' that were to be approved by the given
     * supervisor with id 'supId' that are contained during the 'dateRange'.
     *
     * @param empId Integer - Employee id to retrieve records for
     * @param supId Integer - Supervisor id that managed the set of records we are returning
     * @param dateRange Range<LocalDate> - Returned records will be contained within this date range
     * @return List<TimeRecord>
     */
    List<TimeRecord> getTimeRecordsWithSupervisor(Integer empId, Integer supId, Range<LocalDate> dateRange);

    /**
     * Retrieve time records for which the given supervisor id is the supervisor or supervisor override
     * @param supId int - employee id
     * @param dateRange Range<LocalDate> - date range to query over
     * @param statuses Set<TimeRecordStatus> - time record statuses to retrieve
     * @return ListMultimap<Integer, TimeRecord> - Mapping of original supervisor id -> time records under that supervisor
     */
    ListMultimap<Integer, TimeRecord> getSupervisorRecords(int supId, Range<LocalDate> dateRange,
                                                           Set<TimeRecordStatus> statuses) throws SupervisorException;

    /**
     * @see #getSupervisorRecords(int, Range, Set)
     * An overload that gets supervisor employee records that are still in progress i.e. not approved by personnel
     */
    default ListMultimap<Integer, TimeRecord> getSupervisorRecords(int supId, Range<LocalDate> dateRange)
            throws SupervisorException {
        return getSupervisorRecords(supId, dateRange, TimeRecordStatus.inProgress());
    }

    /**
     *
     * @param record - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    boolean saveRecord(TimeRecord record);
}
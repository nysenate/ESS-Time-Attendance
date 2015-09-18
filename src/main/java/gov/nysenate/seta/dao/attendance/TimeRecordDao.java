package gov.nysenate.seta.dao.attendance;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public interface TimeRecordDao extends BaseDao
{
    /** --- Retrieval methods --- */

    /**
     * Retrieves the time record with the given time record id
     * @param timeRecordId BigInteger
     * @return Time Record
     * @throws EmptyResultDataAccessException if no time record exists with the given id
     */
    TimeRecord getTimeRecord(BigInteger timeRecordId) throws EmptyResultDataAccessException;

    /**
     * Retrieves a list of time records for the employees in the given list during a time range.
     * @param empIds List<Integer> - List of employee ids
     * @param dateRange Range<LocalDate> - date range for which to  retrieve records
     *@param statuses Set<TimeRecordStatus> - The set of statuses to filter by  @return ListMultimap<Integer, TimeRecord>
     */
    ListMultimap<Integer, TimeRecord> getRecordsDuring(Set<Integer> empIds, Range<LocalDate> dateRange,
                                                       Set<TimeRecordStatus> statuses);

    /**
     * Retrieves all time records (for any employee or status), during the specified date range
     * @param dateRange Range<LocalDate>
     * @param statuses Set<TimeRecordStatus>
     * @return
     */
    ListMultimap<Integer, TimeRecord> getRecordsDuring(Range<LocalDate> dateRange);

    /**
     * Gets the distinct years that an employee has at least one time record for.
     * @param empId Integer - employee id
     * @param yearOrder - SortOrder - order the returned years
     * @return List<Integer>
     */
    List<Integer> getTimeRecordYears(Integer empId, SortOrder yearOrder);

    /**
     * @see #getRecordsDuring(Set, Range, Set)
     * Use this overload if you don't want to filter by statuses.
     */
    default ListMultimap<Integer, TimeRecord> getRecordsDuring(Set<Integer> empIds, Range<LocalDate> dateRange) {
        return getRecordsDuring(empIds, dateRange, TimeRecordStatus.getAll());
    }

    /**
     * @see #getRecordsDuring(Set, Range, Set)
     * Use this overload if you just want the records of a single employee
     */
    default List<TimeRecord> getRecordsDuring(int empId, Range<LocalDate> dateRange, Set<TimeRecordStatus> statuses) {
        return getRecordsDuring(Collections.singleton(empId), dateRange, statuses).get(empId);
    }

    /**
     * @see #getRecordsDuring(int, Range, Set)
     * Use this overload if you don't want to filter by statuses and want records from a single employee.
     */
    default List<TimeRecord> getRecordsDuring(int empId, Range<LocalDate> dateRange) {
        return getRecordsDuring(empId, dateRange, TimeRecordStatus.getAll());
    }

    /** --- Insert/Update methods --- */

    /**
     * Update TimeRecord to the Timesheet Table
     * @param record - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    boolean saveRecord(TimeRecord record);

    /**
     * Remove a TimeRecord from the database
     */
    boolean deleteRecord(BigInteger recordId);
}

package gov.nysenate.seta.service.attendance;

import com.google.common.collect.ListMultimap;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.period.PayPeriod;

import java.time.LocalDate;

public interface TimeRecordService
{
    /**
     * Get a multimap containing a mapping of pay periods to the active time records that they are associated with.
     *
     * @param empId int - Employee Id
     * @param endDate LocalDate - The latest date for retrieving active records
     * @return ListMultimap<PayPeriod, TimeRecord>
     * @throws Exception
     */
    public ListMultimap<PayPeriod, TimeRecord> getActiveRecords(int empId, LocalDate endDate) throws Exception;

    /**
     *
     * @param record - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    public boolean saveRecord(TimeRecord record);
}
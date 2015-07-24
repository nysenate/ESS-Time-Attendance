package gov.nysenate.seta.service.attendance;

import com.google.common.collect.Range;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordScope;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Map;

public interface TimeRecordService
{
    Map<TimeRecordScope, TimeRecord> getTimeRecords(int empId, Range<LocalDate> dateRange,
                                                    EnumSet<TimeRecordStatus> statuses, boolean fillMissingRecords)
        throws Exception;

    /**
     *
     * @param record - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    boolean saveRecord(TimeRecord record);
}
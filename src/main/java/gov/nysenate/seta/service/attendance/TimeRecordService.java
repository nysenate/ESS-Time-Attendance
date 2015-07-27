package gov.nysenate.seta.service.attendance;

import com.google.common.collect.Range;
import com.google.common.collect.TreeMultimap;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordScope;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TimeRecordService
{
    List<TimeRecord> getTimeRecords(Set<Integer> empIds, Range<LocalDate> dateRange,
                                    Set<TimeRecordStatus> statuses,
                                    boolean fillMissingRecords);

    /**
     *
     * @param record - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    boolean saveRecord(TimeRecord record);
}
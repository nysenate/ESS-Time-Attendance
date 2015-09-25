package gov.nysenate.seta.dao.attendance;

import com.google.common.collect.RangeSet;

import java.time.LocalDate;
import java.util.SortedSet;

public interface AttendanceDao {

    public SortedSet<Integer> getOpenAttendanceYears(Integer empId);

    public RangeSet<LocalDate> getOpenDates(Integer empId);
}

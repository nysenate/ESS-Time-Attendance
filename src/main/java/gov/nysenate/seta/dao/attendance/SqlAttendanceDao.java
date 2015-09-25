package gov.nysenate.seta.dao.attendance;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class SqlAttendanceDao extends SqlBaseDao implements AttendanceDao {

    @Override
    public SortedSet<Integer> getOpenAttendanceYears(Integer empId) {
        return new TreeSet<>(
                remoteNamedJdbc.query(SqlAttendanceQuery.GET_OPEN_ATTENDANCE_YEARS.getSql(schemaMap()),
                        new MapSqlParameterSource("empId", empId), ((rs, rowNum) -> rs.getInt("DTPERIODYEAR")))
        );
    }

    @Override
    public RangeSet<LocalDate> getOpenDates(Integer empId) {
        RangeSet<LocalDate> activeDates = TreeRangeSet.create();
        getOpenAttendanceYears(empId).stream()
                .map(year -> LocalDate.now().getYear() == year
                        ? Range.closedOpen(LocalDate.ofYearDay(year, 1), LocalDate.now().plusDays(2))
                        : Range.closedOpen(LocalDate.ofYearDay(year, 1), LocalDate.ofYearDay(year + 1, 1)))
                .forEach(activeDates::add);
        return activeDates;
    }
}

package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by riken on 4/2/14.
 */
public class RemoteEntryRowMapper implements RowMapper<TimeEntry> {
    @Override
    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeEntry te = new TimeEntry();
        te.settDayId(rs.getBigDecimal("NUXRDAY"));
        te.setTimesheetId(rs.getBigDecimal("NUXRTIMESHEET"));
        te.setEmpId(rs.getBigDecimal("NUXREFEM"));
        te.setDate(rs.getDate("DTDay"));
        te.setWorkHours(rs.getBigDecimal("NUWORK"));
        te.setTravelHours(rs.getBigDecimal("NUTRAVEL"));
        te.setHolidayHours(rs.getBigDecimal("NUHOLIDAY"));
        te.setVacationHours(rs.getBigDecimal("NUVACATION"));
        te.setPersonalHours(rs.getBigDecimal("NUPERSONAL"));
        te.setSickEmpHours(rs.getBigDecimal("NUSICKEMP"));
        te.setSickFamHours(rs.getBigDecimal("NUSICKFAM"));
        te.setMiscHours(rs.getBigDecimal("NUMISC"));
        if (rs.getString("NUXRMISC") != null) te.setMiscType(MiscLeaveType.valueOfCode(rs.getString("NUXRMISC")));
        te.settOriginalUserId(rs.getString("NATXNORGUSER"));
        te.settUpdateUserId(rs.getString("NATXNUPDUSER"));
        te.settOriginalDate(rs.getTimestamp("DTTXNORIGIN"));
        te.settUpdateDate(rs.getTimestamp("DTTXNUPDATE"));
        te.setActive(rs.getString("CDSTATUS").equals("A"));
        te.setEmpComment(rs.getString("DECOMMENTS"));
        te.setPayType(PayType.valueOf(rs.getString("CDPAYTYPE")));
        return te;
}
}
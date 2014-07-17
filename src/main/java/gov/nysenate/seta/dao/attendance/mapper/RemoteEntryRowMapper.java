package gov.nysenate.seta.dao.attendance.mapper;

import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoteEntryRowMapper implements RowMapper<TimeEntry>
{
    private String pfx = "";

    public RemoteEntryRowMapper() {}

    public RemoteEntryRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeEntry te = new TimeEntry();
        te.setEntryId(rs.getString(pfx + "NUXRDAY"));
        te.setTimeRecordId(rs.getString(pfx + "NUXRTIMESHEET"));
        te.setEmpId(rs.getInt(pfx + "NUXREFEM"));
        te.setDate(rs.getDate(pfx + "DTDAY"));
        te.setWorkHours(rs.getBigDecimal(pfx + "NUWORK"));
        te.setTravelHours(rs.getBigDecimal(pfx + "NUTRAVEL"));
        te.setHolidayHours(rs.getBigDecimal(pfx + "NUHOLIDAY"));
        te.setVacationHours(rs.getBigDecimal(pfx + "NUVACATION"));
        te.setPersonalHours(rs.getBigDecimal(pfx + "NUPERSONAL"));
        te.setSickEmpHours(rs.getBigDecimal(pfx + "NUSICKEMP"));
        te.setSickFamHours(rs.getBigDecimal(pfx + "NUSICKFAM"));
        te.setMiscHours(rs.getBigDecimal(pfx + "NUMISC"));
        if (rs.getString(pfx + "NUXRMISC") != null) {
            te.setMiscType(MiscLeaveType.valueOfCode(rs.getString(pfx + "NUXRMISC")));
        }
        te.setTxOriginalUserId(rs.getString(pfx + "NATXNORGUSER"));
        te.setTxUpdateUserId(rs.getString(pfx + "NATXNUPDUSER"));
        te.setTxOriginalDate(rs.getTimestamp(pfx + "DTTXNORIGIN"));
        te.setTxUpdateDate(rs.getTimestamp(pfx + "DTTXNUPDATE"));
        te.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        te.setEmpComment(rs.getString(pfx + "DECOMMENTS"));
        te.setPayType(PayType.valueOf(rs.getString(pfx + "CDPAYTYPE")));
        return te;
}
}
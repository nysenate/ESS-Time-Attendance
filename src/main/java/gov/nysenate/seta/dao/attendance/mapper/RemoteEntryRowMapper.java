package gov.nysenate.seta.dao.attendance.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoteEntryRowMapper extends BaseRowMapper<TimeEntry>{
    private String pfx = "";

    public RemoteEntryRowMapper() {}

    public RemoteEntryRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeEntry te = new TimeEntry();
        te.setEntryId(rs.getBigDecimal(pfx + "NUXRDAY").toBigInteger());
        te.setTimeRecordId(rs.getBigDecimal(pfx + "NUXRTIMESHEET").toBigInteger());
        te.setEmpId(rs.getInt(pfx + "NUXREFEM"));
        te.setEmployeeName(rs.getString(pfx + "NAUSER"));
        te.setDate(getLocalDateFromRs(rs, pfx + "DTDAY"));
        te.setWorkHours(rs.getInt(pfx + "NUWORK"));
        te.setTravelHours(rs.getInt(pfx + "NUTRAVEL"));
        te.setHolidayHours(rs.getInt(pfx + "NUHOLIDAY"));
        te.setVacationHours(rs.getInt(pfx + "NUVACATION"));
        te.setPersonalHours(rs.getInt(pfx + "NUPERSONAL"));
        te.setSickEmpHours(rs.getInt(pfx + "NUSICKEMP"));
        te.setSickFamHours(rs.getInt(pfx + "NUSICKFAM"));
        te.setMiscHours(rs.getInt(pfx + "NUMISC"));
        if (rs.getString(pfx + "NUXRMISC") != null) {
            te.setMiscType(MiscLeaveType.valueOfId(rs.getBigDecimal(pfx + "NUXRMISC").toBigInteger()));
        }
        te.setTxOriginalUserId(rs.getString(pfx + "NATXNORGUSER"));
        te.setTxUpdateUserId(rs.getString(pfx + "NATXNUPDUSER"));
        te.setTxOriginalDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNORIGIN"));
        te.setTxUpdateDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNUPDATE"));
        te.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        te.setEmpComment(rs.getString(pfx + "DECOMMENTS"));
        te.setPayType(PayType.valueOf(rs.getString(pfx + "CDPAYTYPE")));
        return te;
    }
}

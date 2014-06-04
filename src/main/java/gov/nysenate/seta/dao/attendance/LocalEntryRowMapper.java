package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalEntryRowMapper implements RowMapper<TimeEntry>
{
    private String pfx="";

    public LocalEntryRowMapper(String pfx){
        this.pfx = pfx;
    }

    @Override
    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeEntry entry = new TimeEntry();
        entry.settDayId(rs.getBigDecimal(pfx + "time_entry_id"));
        entry.setTimesheetId(rs.getBigDecimal(pfx + "time_record_id"));
        entry.setEmpId(rs.getBigDecimal(pfx + "emp_id"));
        entry.setDate(rs.getDate(pfx + "day_date"));
        entry.setWorkHours(rs.getBigDecimal(pfx + "work_hr"));
        entry.setTravelHours(rs.getBigDecimal(pfx + "travel_hr"));
        entry.setHolidayHours(rs.getBigDecimal(pfx + "holiday_hr"));
        entry.setSickEmpHours(rs.getBigDecimal(pfx + "sick_emp_hr"));
        entry.setSickFamHours(rs.getBigDecimal(pfx + "sick_family_hr"));
        entry.setMiscHours(rs.getBigDecimal(pfx + "misc_hr"));
        if (rs.getString(pfx + "misc_type") != null) entry.setMiscType(MiscLeaveType.valueOf(rs.getString(pfx + "misc_type")));
        entry.settOriginalUserId(rs.getString(pfx + "t_original_user"));
        entry.settUpdateUserId(rs.getString(pfx + "t_update_user"));
        entry.settOriginalDate(rs.getTimestamp(pfx + "t_original_date"));
        entry.settUpdateDate(rs.getTimestamp(pfx + "t_update_date"));
        entry.setActive(rs.getString(pfx + "status").equals("A"));
        entry.setEmpComment(rs.getString(pfx + "emp_comment"));
        entry.setPayType(PayType.valueOf(rs.getString(pfx + "pay_type")));
        entry.setVacationHours(rs.getBigDecimal(pfx + "vacation_hr"));
        entry.setPersonalHours(rs.getBigDecimal(pfx + "personal_hr"));

        return entry;
    }
}
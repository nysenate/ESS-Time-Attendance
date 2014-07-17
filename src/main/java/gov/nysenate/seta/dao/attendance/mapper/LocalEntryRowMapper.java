package gov.nysenate.seta.dao.attendance.mapper;

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
        entry.setEntryId(rs.getString(pfx + "time_entry_id"));
        entry.setTimeRecordId(rs.getString(pfx + "time_record_id"));
        entry.setEmpId(rs.getInt(pfx + "emp_id"));
        entry.setDate(rs.getDate(pfx + "day_date"));
        entry.setWorkHours(rs.getBigDecimal(pfx + "work_hr"));
        entry.setTravelHours(rs.getBigDecimal(pfx + "travel_hr"));
        entry.setHolidayHours(rs.getBigDecimal(pfx + "holiday_hr"));
        entry.setSickEmpHours(rs.getBigDecimal(pfx + "sick_emp_hr"));
        entry.setSickFamHours(rs.getBigDecimal(pfx + "sick_family_hr"));
        entry.setMiscHours(rs.getBigDecimal(pfx + "misc_hr"));
        if (rs.getString(pfx + "misc_type") != null) {
            entry.setMiscType(MiscLeaveType.valueOf(rs.getString(pfx + "misc_type")));
        }
        entry.setTxOriginalUserId(rs.getString(pfx + "tx_original_user"));
        entry.setTxUpdateUserId(rs.getString(pfx + "tx_update_user"));
        entry.setTxOriginalDate(rs.getTimestamp(pfx + "tx_original_date"));
        entry.setTxUpdateDate(rs.getTimestamp(pfx + "tx_update_date"));
        entry.setActive(rs.getString(pfx + "status").equals("A"));
        entry.setEmpComment(rs.getString(pfx + "emp_comment"));
        entry.setPayType(PayType.valueOf(rs.getString(pfx + "pay_type")));
        entry.setVacationHours(rs.getBigDecimal(pfx + "vacation_hr"));
        entry.setPersonalHours(rs.getBigDecimal(pfx + "personal_hr"));
        return entry;
    }
}
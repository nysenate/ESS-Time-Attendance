package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.MiscLeaveType;
import gov.nysenate.seta.model.PayType;
import gov.nysenate.seta.model.TimeEntry;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by riken on 3/11/14.
 */
public class TimeEntryRowMapper implements RowMapper<TimeEntry> {

    private String pfx="";

    public TimeEntryRowMapper(String pfx){
        this.pfx = pfx;
    }


    @Override
    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeEntry entry = new TimeEntry();
        entry.settDayId(rs.getInt(pfx + "TSDayId"));
        entry.setTimesheetId(rs.getInt(pfx + "TimesheetId"));
        entry.setEmpId(rs.getInt(pfx + "EmpId"));
        entry.setDate(rs.getDate(pfx + "DayDate"));
        entry.setWorkHours(rs.getBigDecimal(pfx + "WorkHR"));
        entry.setTravelHours(rs.getBigDecimal(pfx + "TravelHR"));
        entry.setHolidayHours(rs.getBigDecimal(pfx + "HolidayHR"));
        entry.setSickEmpHours(rs.getBigDecimal(pfx + "SickEmpHR"));
        entry.setSickFamHours(rs.getBigDecimal(pfx + "SickFamHR"));
        entry.setMiscHours(rs.getBigDecimal(pfx + "MiscHR"));
        entry.setMiscType(MiscLeaveType.valueOf(rs.getString(pfx + "MiscTypeId")));
        entry.settOriginalUserId(rs.getInt(pfx + "TOriginalUserId"));
        entry.settUpdateUserId(rs.getInt(pfx + "TUpdateUserId"));
        entry.settOriginalDate(rs.getTimestamp(pfx + "TOriginalDate"));
        entry.settUpdateDate(rs.getTimestamp(pfx + "TUpdateDate"));
        entry.setActive(rs.getString(pfx + "Status").equals("A"));
        entry.setEmpComment(rs.getString(pfx + "EmpComment"));
        entry.setPayType(PayType.valueOf(rs.getString(pfx + "PayType")));
        entry.setVacationHours(rs.getBigDecimal(pfx + "VacationHR"));
        entry.setPersonalHours(rs.getBigDecimal(pfx + "PersonalHR"));

        return entry;
    }
}

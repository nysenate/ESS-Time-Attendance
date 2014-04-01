package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.PayType;
import gov.nysenate.seta.model.TimeRecord;
import gov.nysenate.seta.model.TimeRecordStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by riken on 3/4/14.
 */
public class TimeRecordRowMapper implements RowMapper<TimeRecord>
{
    private String pfx="";

    public TimeRecordRowMapper(String pfx){
        this.pfx = pfx;
    }

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord trec = new TimeRecord();
        trec.setTimesheetId(rs.getBigDecimal(pfx + "timesheet_id"));
        trec.setEmployeeId(rs.getBigDecimal(pfx + "emp_id"));
        trec.settOriginalUserId(rs.getString(pfx + "t_original_user"));
        trec.settUpdateUserId(rs.getString(pfx + "t_update_user"));
        trec.settOriginalDate(rs.getTimestamp(pfx + "t_original_date"));
        trec.settUpdateDate(rs.getTimestamp(pfx + "t_update_date"));
        trec.setActive(rs.getString(pfx + "status").equals("A"));
        trec.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString(pfx + "ts_status_id")));
        trec.setBeginDate(rs.getDate(pfx + "begin_date"));
        trec.setEndDate(rs.getDate(pfx + "end_date"));
        trec.setRemarks(rs.getString(pfx + "remarks"));
        trec.setExeDetails(rs.getString(pfx + "exc_details"));
        trec.setProDate(rs.getDate(pfx + "proc_date"));

        return trec;
    }
}

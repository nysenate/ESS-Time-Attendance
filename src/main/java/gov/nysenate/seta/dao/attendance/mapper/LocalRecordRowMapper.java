package gov.nysenate.seta.dao.attendance.mapper;

import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalRecordRowMapper implements RowMapper<TimeRecord>
{
    private String pfx = "";

    public LocalRecordRowMapper(String pfx){
        this.pfx = pfx;
    }

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord record = new TimeRecord();
        record.setTimeRecordId(rs.getString(pfx + "time_record_id"));
        record.setEmployeeId(rs.getInt(pfx + "emp_id"));
        record.setTxOriginalUserId(rs.getString(pfx + "t_original_user"));
        record.setTxUpdateUserId(rs.getString(pfx + "t_update_user"));
        record.setTxOriginalDate(rs.getTimestamp(pfx + "t_original_date"));
        record.setTxUpdateDate(rs.getTimestamp(pfx + "t_update_date"));
        record.setActive(rs.getString(pfx + "status").equals("A"));
        record.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString(pfx + "ts_status_id")));
        record.setBeginDate(rs.getDate(pfx + "begin_date"));
        record.setEndDate(rs.getDate(pfx + "end_date"));
        record.setRemarks(rs.getString(pfx + "remarks"));
        record.setExceptionDetails(rs.getString(pfx + "exc_details"));
        record.setProcessedDate(rs.getDate(pfx + "proc_date"));
        return record;
    }
}

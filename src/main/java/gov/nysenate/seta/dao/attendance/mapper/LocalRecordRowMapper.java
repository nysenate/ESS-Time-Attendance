package gov.nysenate.seta.dao.attendance.mapper;

import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalRecordRowMapper implements RowMapper<TimeRecord>
{
    private String pfx="";

    public LocalRecordRowMapper(String pfx){
        this.pfx = pfx;
    }

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord trec = new TimeRecord();
        trec.setTimeRecordId(rs.getBigDecimal(pfx + "time_record_id"));
        trec.setEmployeeId(rs.getBigDecimal(pfx + "emp_id"));
        trec.setTxOriginalUserId(rs.getString(pfx + "t_original_user"));
        trec.setTxUpdateUserId(rs.getString(pfx + "t_update_user"));
        trec.setTxOriginalDate(rs.getTimestamp(pfx + "t_original_date"));
        trec.setTxUpdateDate(rs.getTimestamp(pfx + "t_update_date"));
        trec.setActive(rs.getString(pfx + "status").equals("A"));
        trec.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString(pfx + "ts_status_id")));
        trec.setBeginDate(rs.getDate(pfx + "begin_date"));
        trec.setEndDate(rs.getDate(pfx + "end_date"));
        trec.setRemarks(rs.getString(pfx + "remarks"));
        trec.setExceptionDetails(rs.getString(pfx + "exc_details"));
        trec.setProcessedDate(rs.getDate(pfx + "proc_date"));

        return trec;
    }
}

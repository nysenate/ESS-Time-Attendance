package gov.nysenate.seta.dao.attendance.mapper;

import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoteRecordRowMapper implements RowMapper<TimeRecord>
{
    private String pfx = "";

    public RemoteRecordRowMapper() {}

    public RemoteRecordRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord record = new TimeRecord();
        record.setTimeRecordId(rs.getBigDecimal(pfx + "NUXRTIMESHEET"));
        record.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        record.setTxOriginalUserId(rs.getString(pfx + "NATXNORGUSER"));
        record.setTxUpdateUserId(rs.getString(pfx + "NATXNUPDUSER"));
        record.setTxOriginalDate(rs.getTimestamp(pfx + "DTTXNORIGIN"));
        record.setTxUpdateDate(rs.getTimestamp(pfx + "DTTXNUPDATE"));
        record.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        record.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString(pfx + "CDTSSTAT")));
        record.setBeginDate(rs.getDate(pfx + "DTBEGIN"));
        record.setEndDate(rs.getDate(pfx + "DTEND"));
        record.setRemarks(rs.getString(pfx + "DEREMARKS"));
        record.setSupervisorId(rs.getBigDecimal(pfx + "NUXREFSV"));
        record.setExceptionDetails(rs.getString(pfx + "DEEXCEPTION"));
        record.setProcessedDate(rs.getDate(pfx + "DTPROCESS"));
        return record;
    }
}

package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoteRecordRowMapper implements RowMapper<TimeRecord>
{
    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord tr = new TimeRecord();
        tr.setTimeRecordId(rs.getBigDecimal("NUXRTIMESHEET"));
        tr.setEmployeeId(rs.getBigDecimal("NUXREFEM"));
        tr.setTxOriginalUserId(rs.getString("NATXNORGUSER"));
        tr.setTxUpdateUserId(rs.getString("NATXNUPDUSER"));
        tr.setTxOriginalDate(rs.getTimestamp("DTTXNORIGIN"));
        tr.setTxUpdateDate(rs.getTimestamp("DTTXNUPDATE"));
        tr.setActive(rs.getString("CDSTATUS").equals("A"));
        tr.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString("CDTSSTAT")));
        tr.setBeginDate(rs.getDate("DTBEGIN"));
        tr.setEndDate(rs.getDate("DTEND"));
        tr.setRemarks(rs.getString("DEREMARKS"));
        tr.setSupervisorId(rs.getBigDecimal("NUXREFSV"));
        tr.setExceptionDetails(rs.getString("DEEXCEPTION"));
        tr.setProcessedDate(rs.getDate("DTPROCESS"));

        return tr;
    }
}

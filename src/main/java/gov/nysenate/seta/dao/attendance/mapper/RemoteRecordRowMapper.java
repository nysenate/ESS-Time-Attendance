package gov.nysenate.seta.dao.attendance.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoteRecordRowMapper extends BaseRowMapper<TimeRecord>
{
    private String pfx = "";

    public RemoteRecordRowMapper() {}

    public RemoteRecordRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord record = new TimeRecord();
        record.setTimeRecordId(rs.getBigDecimal(pfx + "NUXRTIMESHEET").toBigInteger());
        record.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        record.setTxOriginalUserId(rs.getString(pfx + "NATXNORGUSER"));
        record.setEmployeeName(rs.getString(pfx + "NAUSER"));
        record.setTxUpdateUserId(rs.getString(pfx + "NATXNUPDUSER"));
        record.setTxOriginalDate(getLocalDateTime(rs,(pfx + "DTTXNORIGIN")));
        record.setTxUpdateDate(getLocalDateTime(rs,(pfx + "DTTXNUPDATE")));
        record.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        record.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString(pfx + "CDTSSTAT")));
        record.setBeginDate(getLocalDate(rs, pfx + "DTBEGIN"));
        record.setEndDate(getLocalDate(rs, "DTEND"));
        record.setRemarks(rs.getString(pfx + "DEREMARKS"));
        record.setSupervisorId(rs.getInt(pfx + "NUXREFSV"));
        record.setExceptionDetails(rs.getString(pfx + "DEEXCEPTION"));
        record.setProcessedDate(getLocalDate(rs, pfx + "DTPROCESS"));
        return record;
    }
}

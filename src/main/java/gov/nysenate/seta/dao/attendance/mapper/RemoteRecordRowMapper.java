package gov.nysenate.seta.dao.attendance.mapper;

import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class RemoteRecordRowMapper extends BaseRowMapper<TimeRecord>
{

    @Autowired
    PayPeriodDao payPeriodDao;

    private String pfx = "";        // TODO is this necessary ??

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord record = new TimeRecord();
        record.setBeginDate(DateUtils.getLocalDate(rs.getDate("DTBEGIN")));
        record.setEndDate(DateUtils.getLocalDate(rs.getDate("DTEND")));
        record.setTimeRecordId(rs.getBigDecimal(pfx + "NUXRTIMESHEET").toBigInteger());
        record.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        record.setOriginalUserId(rs.getString(pfx + "NATXNORGUSER"));
        record.setEmployeeName(rs.getString(pfx + "NAUSER"));
        record.setUpdateUserId(rs.getString(pfx + "NATXNUPDUSER"));
        record.setCreatedDate(getLocalDateTimeFromRs(rs, (pfx + "DTTXNORIGIN")));
        record.setUpdateDate(getLocalDateTimeFromRs(rs, (pfx + "DTTXNUPDATE")));
        record.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        record.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString(pfx + "CDTSSTAT")));
        record.setRemarks(rs.getString(pfx + "DEREMARKS"));
        record.setSupervisorId(rs.getInt(pfx + "NUXREFSV"));
        record.setExceptionDetails(rs.getString(pfx + "DEEXCEPTION"));
        record.setProcessedDate(getLocalDateFromRs(rs, pfx + "DTPROCESS"));
        record.setRespHeadCode(rs.getString(pfx + "CDRESPCTRHD"));
        return record;
    }
}

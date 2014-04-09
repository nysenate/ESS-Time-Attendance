package gov.nysenate.seta.dao.mapper;

import gov.nysenate.seta.model.TimeRecord;
import gov.nysenate.seta.model.TimeRecordStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by riken on 4/2/14.
 */
public class RemoteRecordRowMapper implements RowMapper<TimeRecord>{

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord tr = new TimeRecord();
        tr.setTimesheetId(rs.getBigDecimal("NUXRTIMESHEET"));
        tr.setEmployeeId(rs.getBigDecimal("NUXREFEM"));
        tr.settOriginalUserId(rs.getString("NATXNORGUSER"));
        tr.settUpdateUserId(rs.getString("NATXNUPDUSER"));
        tr.settOriginalDate(rs.getTimestamp("DTTXNORIGIN"));
        tr.settUpdateDate(rs.getTimestamp("DTTXNUPDATE"));
        tr.setActive(rs.getString("CDSTATUS").equals("A"));
        tr.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString("CDTSSTAT")));
        tr.setBeginDate(rs.getDate("DTBEGIN"));
        tr.setEndDate(rs.getDate("DTEND"));
        tr.setRemarks(rs.getString("DEREMARKS"));
        tr.setSupervisorId(rs.getBigDecimal("NUXREFSV"));
        tr.setExeDetails(rs.getString("DEEXCEPTION"));
        tr.setProDate(rs.getDate("DTPROCESS"));

        return tr;
    }
}

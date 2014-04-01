package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SqlMigrationDao extends SqlBaseDao implements MigrationDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlMigrationDao.class);

    public static final String GET_REMOTE_TIME_RECORD =
            "SELECT * \n" +
                "FROM \n" +
                    "(SELECT rownum as rn, t1.*  \n" +
                         "FROM \n" +
                             "(SELECT * \n" +
                                  "FROM " +
                                      "PM23TIMESHEET t ORDER BY DTTXNORIGIN \n" +
                            ") t1 \n" +
                    ") \n"+
                "WHERE rn > :rowNumber and rn <= :threshold";

    public static final String GET_REMOTE_TIME_ENTRY =
            "SELECT * \n" +
                "FROM \n" +
                    "(SELECT rownum as rn, t1.*  \n" +
                         "FROM \n" +
                             "(SELECT * \n" +
                                  "FROM " +
                                      "PD23TIMESHEET t ORDER BY DTTXNORIGIN \n" +
                             ") t1 \n" +
                    ") \n"+
                "WHERE rn > :rowNumber and rn <= :threshold";


    @Override
    public List<TimeRecord> getRemoteTimeRecord(int rowNumber, int threshold) throws TimeRecordNotFoundException {

        List<TimeRecord> timeRecordList = null;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rowNumber", rowNumber);
        params.addValue("threshold", threshold);

        try {
            timeRecordList = remoteNamedJdbc.query(GET_REMOTE_TIME_RECORD, params, new RowMapper<TimeRecord>() {
                @Override
                public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TimeRecord tr = new TimeRecord();
                    tr.setTimesheetId(rs.getInt("NUXRTIMESHEET"));
                    tr.setEmployeeId(rs.getInt("NUXREFEM"));
                    tr.settOriginalUserId(rs.getString("NATXNORGUSER"));
                    tr.settUpdateUserId(rs.getString("NATXNUPDUSER"));
                    tr.settOriginalDate(rs.getTimestamp("DTTXNORIGIN"));
                    tr.settUpdateDate(rs.getTimestamp("DTTXNUPDATE"));
                    tr.setActive(rs.getString("CDSTATUS").equals("A"));
                    tr.setRecordStatus(TimeRecordStatus.valueOf(rs.getString("CDTSSTAT")));
                    tr.setBeginDate(rs.getDate("DTBEGIN"));
                    tr.setEndDate(rs.getDate("DTEND"));
                    tr.setPayType(PayType.valueOf(rs.getString("CDPAYTYPE")));
                    tr.setRemarks(rs.getString("DEREMARKS"));
                    tr.setSupervisorId(rs.getInt("NUXREFSUP"));
                    tr.setExeDetails(rs.getString("DEEXCEPTION"));
                    tr.setProDate(rs.getDate("DTPROCESS"));

                    return tr;
                }
            });
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records where rownumber : {} error: {}", rowNumber, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for Row Number Range : " + rowNumber + "-" + threshold);
        }

        return timeRecordList;
    }

    @Override
    public List<TimeEntry> getRemoteTimeEntry(int rowNumber, int threshold) throws TimeEntryNotFoundEx {

        List<TimeEntry> timeEntryList = null;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rowNumber", rowNumber);
        params.addValue("threshold", threshold);

        try {
            timeEntryList = remoteNamedJdbc.query(GET_REMOTE_TIME_ENTRY, params, new RowMapper<TimeEntry>() {
                @Override
                public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TimeEntry te = new TimeEntry();
                    te.settDayId(rs.getInt("NUXRDAY"));
                    te.setTimesheetId(rs.getInt("NUXRTIMESHEET"));
                    te.setEmpId(rs.getInt("NUXREFEM"));
                    te.setDate(rs.getDate("DTDay"));
                    te.setWorkHours(rs.getBigDecimal("NUWORK"));
                    te.setTravelHours(rs.getBigDecimal("NUTRAVEL"));
                    te.setHolidayHours(rs.getBigDecimal("NUHOLIDAY"));
                    te.setVacationHours(rs.getBigDecimal("NUVACATION"));
                    te.setPersonalHours(rs.getBigDecimal("NUPERSONAL"));
                    te.setSickEmpHours(rs.getBigDecimal("NUSICKEMP"));
                    te.setSickFamHours(rs.getBigDecimal("NUSICKFAM"));
                    te.setMiscHours(rs.getBigDecimal("NUMISC"));
                    te.setMiscType(MiscLeaveType.valueOf(rs.getString("NUXRMISC")));
                    te.settOriginalUserId(rs.getString("NATXNORGUSER"));
                    te.settUpdateUserId(rs.getString("NATXNUPDUSER"));
                    te.settOriginalDate(rs.getTimestamp("DTTXNORIGIN"));
                    te.settUpdateDate(rs.getTimestamp("DTTXNUPDATE"));
                    te.setActive(rs.getString("CDSTATUS").equals("A"));
                    te.setEmpComment(rs.getString("DECOMMENTS"));
                    te.setPayType(PayType.valueOf(rs.getString("CDPAYTYPE")));

                    return te;
                }
            });

        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Entries where rownumber : {} error: {}", rowNumber, ex.getMessage());
            throw new TimeEntryNotFoundEx("No matching Time Records for Row Number Range : " + rowNumber + "-" + threshold);
        }

        return timeEntryList;
    }
}

package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.attendance.mapper.RemoteEntryRowMapper;
import gov.nysenate.seta.dao.attendance.mapper.RemoteRecordRowMapper;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordNotFoundException;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("remoteTimeRecordDao")
public class SqlRemoteRecordDao extends SqlBaseDao implements TimeRecordDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteRecordDao.class);

    protected static final String GET_TIME_REC_SQL_TMPL =
        "SELECT \n" +
        /**   PM23TIMESHEET columns (no alias needed) */
        "    rec.NUXRTIMESHEET, rec.NUXREFEM, rec.NATXNORGUSER, rec.NATXNUPDUSER, rec.DTTXNORIGIN, rec.DTTXNUPDATE, " +
        "    rec.CDSTATUS, rec.CDTSSTAT, rec.DTBEGIN, rec.DTEND, rec.DEREMARKS, rec.NUXREFSV, rec.DEEXCEPTION, " +
        "    rec.DTPROCESS, " +
        /**   PD23TIMESHEET columns (aliased with ENT_) */
        "    ent.NUXRDAY AS ENT_NUXRDAY, ent.NUXRTIMESHEET AS ENT_NUXRTIMESHEET, ent.NUXREFEM AS ENT_NUXREFEM, " +
        "    ent.DTDAY AS ENT_DTDAY, ent.NUWORK AS ENT_NUWORK, ent.NUTRAVEL AS ENT_NUTRAVEL, ent.NUHOLIDAY AS ENT_NUHOLIDAY, " +
        "    ent.NUVACATION AS ENT_NUVACATION, ent.NUPERSONAL AS ENT_NUPERSONAL, ent.NUSICKEMP AS ENT_NUSICKEMP, " +
        "    ent.NUSICKFAM AS ENT_NUSICKFAM, ent.NUMISC AS ENT_NUMISC, ent.NUXRMISC AS ENT_NUXRMISC, ent.NATXNORGUSER AS ENT_NATXNORGUSER," +
        "    ent.NATXNUPDUSER AS ENT_NATXNUPDUSER, ent.DTTXNORIGIN AS ENT_DTTXNORIGIN, ent.DTTXNUPDATE AS ENT_DTTXNUPDATE, " +
        "    ent.CDSTATUS AS ENT_CDSTATUS, ent.DECOMMENTS AS ENT_DECOMMENTS, ent.CDPAYTYPE AS ENT_CDPAYTYPE " +
        "FROM PM23TIMESHEET rec " +
        "LEFT JOIN PD23TIMESHEET ent ON rec.NUXRTIMESHEET = ent.NUXRTIMESHEET \n" +
        "WHERE rec.CDSTATUS = 'A' AND ent.CDSTATUS = 'A' %s \n" +
        "ORDER BY rec.NUXREFEM ASC, rec.DTBEGIN ASC, ent.DTDAY ASC";

    protected static final String GET_TIME_REC_BY_DATES_SQL =
        String.format(GET_TIME_REC_SQL_TMPL,
            "AND rec.NUXREFEM IN (:empIds) AND (:startDate <= TRUNC(rec.DTBEGIN)) AND (:endDate >= TRUNC(rec.DTEND)) " +
            "AND rec.CDTSSTAT IN (:statuses)");

    protected static final String GET_TREC_BY_EMPID_SQL = String.format(GET_TIME_REC_SQL_TMPL, "NUXREFEM = :empId");
    protected static final String GET_TREC_BY_DATE_SQL = String.format(GET_TIME_REC_SQL_TMPL, "DTBEGIN = :startDate AND DTEND = :endDate");
    protected static final String GET_TREC_BY_RECSTATUS_SQL = String.format(GET_TIME_REC_SQL_TMPL, "CDTSSTAT = :tSStatusId AND NUXREFEM = :empId AND DTBEGIN = :startDate AND DTEND = :endDate");

    protected static final String SET_TIME_REC_SQL =
            "INSERT \n" +
                    "INTO PM23TIMESHEET \n" +
                    "(NUXRTIMESHEET , NUXREFEM, NATXNORGUSER, NATXNUPDUSER, DTTXNORIGIN, DTTXNUPDATE, CDSTATUS, CDTSSTAT, DTBEGIN, DTEND, DEREMARKS, NUXREFSV, DEEXCEPTION, DTPROCESS) \n" +
                    "VALUES (:timesheetId, :empId, :tOriginalUserId, :tUpdateUserId, :tOriginalDate, :tUpdateDate, :status, :tSStatusId, :beginDate, :endDate, :remarks, :supervisorId, :excDetails, :procDate) \n";

    protected static final String UPDATE_TIME_REC_SQL =
            "UPDATE ts.timesheet \n" +
                    "SET \n" +
                    "(NUXREFEM = :empId, NATXNORGUSER = :tOriginalUserId, NATXNUPDUSER = :tUpdateUserId, DTTXNORIGIN = :tOriginalDate, DTTXNUPDATE = :tUpdateDate, CDSTATUS = :status, CDTSSTAT = :tSStatusId, DTBEGIN = :beginDate, DTEND = :endDate, DEREMARKS = :remarks, NUXREFSV = :supervisorId, DEEXCEPTION = :excDetails, DTPROCESS = :procDate) \n" +
                    "WHERE timesheet_id = :timesheetId";

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getRecordsDuring(int empId, Date startDate, Date endDate) {
        return getRecordsDuring(empId, startDate, endDate, new HashSet<>(Arrays.asList(TimeRecordStatus.values())));
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getRecordsDuring(int empId, Date startDate, Date endDate, Set<TimeRecordStatus> statuses) {
        Map<Integer, List<TimeRecord>> res = getRecordsDuring(Arrays.asList(empId), startDate, endDate, statuses);
        return res.get(empId);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, List<TimeRecord>> getRecordsDuring(List<Integer> empIds, Date startDate, Date endDate) {
        return getRecordsDuring(empIds, startDate, endDate, new HashSet<>(Arrays.asList(TimeRecordStatus.values())));
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, List<TimeRecord>> getRecordsDuring(List<Integer> empIds, Date startDate, Date endDate, Set<TimeRecordStatus> statuses) {
        Set<String> statusCodes = new HashSet<>();
        for (TimeRecordStatus status : statuses) {
            statusCodes.add(status.getCode());
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empIds", empIds);
        params.addValue("startDate", startDate);
        params.addValue("endDate", endDate);
        params.addValue("statuses", statusCodes);
        TimeRecordRowCallbackHandler handler = new TimeRecordRowCallbackHandler();
        remoteNamedJdbc.query(GET_TIME_REC_BY_DATES_SQL, params, handler);
        return handler.getRecordMap();
    }

    /** --- Helper Classes --- */

    private class TimeRecordRowCallbackHandler implements RowCallbackHandler
    {
        private RemoteRecordRowMapper remoteRecordRowMapper = new RemoteRecordRowMapper();
        private RemoteEntryRowMapper remoteEntryRowMapper = new RemoteEntryRowMapper("ENT_");
        private Map<BigDecimal, TimeRecord> recordMap = new HashMap<>();
        private List<TimeRecord> recordList = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            BigDecimal recordId = rs.getBigDecimal("NUXRTIMESHEET");
            TimeRecord record;
            if (!recordMap.containsKey(recordId)) {
                record = remoteRecordRowMapper.mapRow(rs, 0);
                recordMap.put(recordId, record);
                recordList.add(record);
            }
            else {
                record = recordMap.get(recordId);
            }
            TimeEntry entry = remoteEntryRowMapper.mapRow(rs, 0);
            record.getTimeEntries().add(entry);
        }

        public Map<Integer, List<TimeRecord>> getRecordMap() {
            Map<Integer, List<TimeRecord>> empRecordMap = new HashMap<>();
            for (TimeRecord record : recordList) {
                if (!empRecordMap.containsKey(record.getEmployeeId())) {
                    empRecordMap.put(record.getEmployeeId(), new ArrayList<TimeRecord>());
                }
                empRecordMap.get(record.getEmployeeId()).add(record);
            }
            return empRecordMap;
        }
    }

    public List<TimeRecord> getRecordByEmployeeId(int empId) throws TimeRecordNotFoundException {

        List<TimeRecord> timeRecordList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);

        try{
            timeRecordList = remoteNamedJdbc.query(GET_TREC_BY_EMPID_SQL, params,
                    new RemoteRecordRowMapper());
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records of {} error: {}", empId, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for employee id: " + empId);
        }
        return  timeRecordList;

    }

    public boolean setRecord(TimeRecord tr) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timesheetId", tr.getTimeRecordId());
        params.addValue("empId", tr.getEmployeeId());
        params.addValue("tOriginalUserId", tr.getTxOriginalUserId());
        params.addValue("tUpdateUserId", tr.getTxUpdateUserId());
        params.addValue("tOriginalDate", tr.getTxOriginalDate());
        params.addValue("tUpdateDate", tr.getTxUpdateDate());
        if(tr.isActive()==true){params.addValue("status", "A");}
        else{ params.addValue("status", "I");}
        params.addValue("tSStatusId", tr.getRecordStatus().getCode());
        params.addValue("beginDate", tr.getBeginDate());
        params.addValue("endDate", tr.getEndDate());
        params.addValue("remarks", tr.getRemarks());
        params.addValue("supervisorId", tr.getSupervisorId());
        params.addValue("excDetails", tr.getExceptionDetails());
        params.addValue("procDate", tr.getProcessedDate());

        if (remoteNamedJdbc.update(SET_TIME_REC_SQL, params)==1) {    return true;}
        else{   return false;}

    }

    @Override
    public boolean saveRecord(TimeRecord record) {

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("timesheetId", record.getTimeRecordId());
        params.addValue("empId", record.getEmployeeId());
        params.addValue("tOriginalUserId", record.getTxOriginalUserId());
        params.addValue("tUpdateUserId", record.getTxUpdateUserId());
        params.addValue("tOriginalDate", record.getTxOriginalDate());
        params.addValue("tUpdateDate", record.getTxUpdateDate());
        params.addValue("status", getStatusCode(record.isActive()));
        params.addValue("tSStatusId", record.getRecordStatus().getCode());
        params.addValue("beginDate", record.getBeginDate());
        params.addValue("endDate", record.getEndDate());
        params.addValue("remarks", record.getRemarks());
        params.addValue("supervisorId", record.getSupervisorId());
        params.addValue("excDetails", record.getExceptionDetails());
        params.addValue("procDate", record.getProcessedDate());

        if (remoteNamedJdbc.update(UPDATE_TIME_REC_SQL, params)==1) return true;
        else return false;

    }


    public int getTimeRecordCount(BigDecimal timesheetId) throws TimeRecordNotFoundException {
        return 0;
    }

}




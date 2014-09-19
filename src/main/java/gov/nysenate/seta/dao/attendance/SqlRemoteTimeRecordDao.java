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
public class SqlRemoteTimeRecordDao extends SqlBaseDao implements TimeRecordDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteTimeRecordDao.class);

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
        remoteNamedJdbc.query(SqlRemoteTimeRecordQuery.GET_TIME_REC_BY_DATES_SQL.getSql(), params, handler);
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
            timeRecordList = remoteNamedJdbc.query(SqlRemoteTimeRecordQuery.GET_TREC_BY_EMPID_SQL.getSql(), params,
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

        if (remoteNamedJdbc.update(SqlRemoteTimeRecordQuery.SET_TIME_REC_SQL.getSql(), params)==1) {    return true;}
        else{   return false;}

    }

    @Override
    public boolean saveRecord(TimeRecord record) {

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("timesheetId", new BigDecimal(record.getTimeRecordId()));
        params.addValue("empId", record.getEmployeeId());
        params.addValue("tOriginalUserId", record.getTxOriginalUserId());
        params.addValue("tUpdateUserId", record.getTxUpdateUserId());
        params.addValue("employeeName", record.getEmployeeName());
        params.addValue("tOriginalDate", toDate(record.getTxOriginalDate()));
        params.addValue("tUpdateDate", toDate(record.getTxUpdateDate()));
        params.addValue("status", String.valueOf(getStatusCode(record.isActive())));
        params.addValue("tSStatusId", record.getRecordStatus().getCode());
        params.addValue("beginDate", toDate(record.getBeginDate()));
        params.addValue("endDate", toDate(record.getEndDate()));
        params.addValue("remarks", record.getRemarks());
        params.addValue("supervisorId", record.getSupervisorId());
        params.addValue("excDetails", record.getExceptionDetails());
        params.addValue("procDate", record.getProcessedDate());

        if (remoteNamedJdbc.update(SqlRemoteTimeRecordQuery.UPDATE_TIME_REC_SQL.getSql(), params)==0) {
            if (remoteNamedJdbc.update(SqlRemoteTimeRecordQuery.SET_TIME_REC_SQL.getSql(), params)==0) {
                return false;
            }
        }
        return true;
    }


    public int getTimeRecordCount(BigDecimal timesheetId) throws TimeRecordNotFoundException {
        return 0;
    }

}




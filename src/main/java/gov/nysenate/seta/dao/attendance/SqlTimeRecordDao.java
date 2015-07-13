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
import java.time.LocalDate;
import java.util.*;

@Repository("remoteTimeRecordDao")
public class SqlTimeRecordDao extends SqlBaseDao implements TimeRecordDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlTimeRecordDao.class);

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getRecordsDuring(int empId, LocalDate startDate, LocalDate endDate) {
        return getRecordsDuring(empId, startDate, endDate, new HashSet<>(Arrays.asList(TimeRecordStatus.values())));
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getRecordsDuring(int empId, LocalDate startDate, LocalDate endDate, Set<TimeRecordStatus> statuses) {
        Map<Integer, List<TimeRecord>> res = getRecordsDuring(Arrays.asList(empId), startDate, endDate, statuses);
        return res.get(empId);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, List<TimeRecord>> getRecordsDuring(List<Integer> empIds, LocalDate startDate, LocalDate endDate) {
        return getRecordsDuring(empIds, startDate, endDate, new HashSet<>(Arrays.asList(TimeRecordStatus.values())));
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, List<TimeRecord>> getRecordsDuring(List<Integer> empIds, LocalDate startDate, LocalDate endDate,
                                                           Set<TimeRecordStatus> statuses) {
        Set<String> statusCodes = new HashSet<>();
        for (TimeRecordStatus status : statuses) {
            statusCodes.add(status.getCode());
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empIds", empIds);
        params.addValue("startDate", toDate(startDate));
        params.addValue("endDate", toDate(endDate));
        params.addValue("statuses", statusCodes);
        TimeRecordRowCallbackHandler handler = new TimeRecordRowCallbackHandler();
        remoteNamedJdbc.query(SqlTimeRecordQuery.GET_TIME_REC_BY_DATES.getSql(), params, handler);
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
            timeRecordList = remoteNamedJdbc.query(SqlTimeRecordQuery.GET_TREC_BY_EMPID.getSql(), params,
                    new RemoteRecordRowMapper());
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records of {} error: {}", empId, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for employee id: " + empId);
        }
        return  timeRecordList;

    }

    @Override
    public boolean saveRecord(TimeRecord record) {

        MapSqlParameterSource params = getTimeRecordParams(record);

        if (remoteNamedJdbc.update(SqlTimeRecordQuery.UPDATE_TIME_REC_SQL.getSql(), params)==0) {
            if (remoteNamedJdbc.update(SqlTimeRecordQuery.INSERT_TIME_REC.getSql(), params)==0) {
                return false;
            }
        }
        return true;
    }


    public int getTimeRecordCount(BigDecimal timesheetId) throws TimeRecordNotFoundException {
        return 0;
    }

    public MapSqlParameterSource getTimeRecordParams(TimeRecord timeRecord) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timesheetId", new BigDecimal(timeRecord.getTimeRecordId()));
        params.addValue("empId", timeRecord.getEmployeeId());
        params.addValue("employeeName", timeRecord.getEmployeeName());
        params.addValue("tOriginalUserId", timeRecord.getTxOriginalUserId());
        params.addValue("tUpdateUserId", timeRecord.getTxUpdateUserId());
        params.addValue("tOriginalDate", toDate(timeRecord.getTxOriginalDate()));
        params.addValue("tUpdateDate", toDate(timeRecord.getTxUpdateDate()));
        params.addValue("status", timeRecord.isActive() ? "A" : "I");
        params.addValue("tSStatusId", timeRecord.getRecordStatus().getCode());
        params.addValue("beginDate", toDate(timeRecord.getPayPeriod().getStartDate()));
        params.addValue("endDate", toDate(timeRecord.getPayPeriod().getEndDate()));
        params.addValue("remarks", timeRecord.getRemarks());
        params.addValue("supervisorId", timeRecord.getSupervisorId());
        params.addValue("excDetails", timeRecord.getExceptionDetails());
        params.addValue("procDate", toDate(timeRecord.getProcessedDate()));

        return params;
    }

}




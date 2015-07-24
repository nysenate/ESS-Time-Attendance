package gov.nysenate.seta.dao.attendance;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.dao.attendance.mapper.RemoteEntryRowMapper;
import gov.nysenate.seta.dao.attendance.mapper.RemoteRecordRowMapper;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.period.mapper.PayPeriodRowMapper;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordNotFoundException;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SqlTimeRecordDao extends SqlBaseDao implements TimeRecordDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlTimeRecordDao.class);

    @Autowired private TimeEntryDao timeEntryDao;

    /** {@inheritDoc} */
    @Override
    public TimeRecord getTimeRecord(BigInteger timeRecordId) throws EmptyResultDataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource("timesheetId", String.valueOf(timeRecordId));
        TimeRecordRowCallbackHandler rowHandler = new TimeRecordRowCallbackHandler();
        remoteNamedJdbc.query(SqlTimeRecordQuery.GET_TIME_REC_BY_ID.getSql(), params, rowHandler);
        List<TimeRecord> records = rowHandler.getRecordList();
        if (records.isEmpty()) {
            throw new EmptyResultDataAccessException("could not find time record with id: " + timeRecordId, 1);
        }
        return records.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public ListMultimap<Integer, TimeRecord> getRecordsDuring(Set<Integer> empIds, Range<LocalDate> dateRange,
                                                              Set<TimeRecordStatus> statuses) {
        Set<String> statusCodes = statuses.stream()
                .map(TimeRecordStatus::getCode)
                .collect(Collectors.toSet());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empIds", empIds);
        params.addValue("startDate", toDate(DateUtils.startOfDateRange(dateRange)));
        params.addValue("endDate", toDate(DateUtils.endOfDateRange(dateRange)));
        params.addValue("statuses", statusCodes);
        TimeRecordRowCallbackHandler handler = new TimeRecordRowCallbackHandler();
        remoteNamedJdbc.query(SqlTimeRecordQuery.GET_TIME_REC_BY_DATES.getSql(), params, handler);
        return handler.getRecordMap();
    }

    /** --- Helper Classes --- */

    private static class TimeRecordRowCallbackHandler implements RowCallbackHandler
    {
        private RemoteRecordRowMapper remoteRecordRowMapper = new RemoteRecordRowMapper();
        private RemoteEntryRowMapper remoteEntryRowMapper = new RemoteEntryRowMapper("ENT_");
        private PayPeriodRowMapper periodRowMapper = new PayPeriodRowMapper("PER_");
        private Map<BigDecimal, TimeRecord> recordMap = new HashMap<>();
        private List<TimeRecord> recordList = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            BigDecimal recordId = rs.getBigDecimal("NUXRTIMESHEET");
            TimeRecord record;
            if (!recordMap.containsKey(recordId)) {
                record = remoteRecordRowMapper.mapRow(rs, 0);
                record.setPayPeriod(periodRowMapper.mapRow(rs, 0));
                recordMap.put(recordId, record);
                recordList.add(record);
            }
            else {
                record = recordMap.get(recordId);
            }
            TimeEntry entry = remoteEntryRowMapper.mapRow(rs, 0);
            record.getTimeEntries().add(entry);
        }

        public ListMultimap<Integer, TimeRecord> getRecordMap() {
            ListMultimap<Integer, TimeRecord> empRecordMap = ArrayListMultimap.create();
            recordList.forEach(record -> empRecordMap.put(record.getEmployeeId(), record));
            return empRecordMap;
        }

        public List<TimeRecord> getRecordList() {
            return recordList;
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
        // Insert each entry from the time record
        record.getTimeEntries().forEach(timeEntryDao::updateTimeEntry);
        return true;
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
        params.addValue("beginDate", toDate(timeRecord.getBeginDate()));
        params.addValue("endDate", toDate(timeRecord.getEndDate()));
        params.addValue("remarks", timeRecord.getRemarks());
        params.addValue("supervisorId", timeRecord.getSupervisorId());
        params.addValue("excDetails", timeRecord.getExceptionDetails());
        params.addValue("procDate", toDate(timeRecord.getProcessedDate()));

        return params;
    }

}




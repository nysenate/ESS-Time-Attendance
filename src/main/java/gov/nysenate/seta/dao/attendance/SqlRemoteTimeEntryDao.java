package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.attendance.mapper.RemoteEntryRowMapper;
import gov.nysenate.seta.dao.base.OrderBy;
import gov.nysenate.seta.dao.base.SortOrder;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.attendance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static gov.nysenate.seta.util.DateUtils.toDate;

@Repository("remoteTimeEntry")
public class SqlRemoteTimeEntryDao extends SqlBaseDao implements TimeEntryDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteTimeEntryDao.class);

    @Override
    public TimeEntry getTimeEntryById(BigInteger timeEntryId) throws TimeEntryException {
        return null;
    }

    /** {@inheritDoc}
     * @param timeRecordId*/
    @Override
    public List<TimeEntry> getTimeEntriesByRecordId(BigInteger timeRecordId) throws TimeEntryException {
        List<TimeEntry> timeEntryList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", "A");
        params.addValue("timesheetId", new BigDecimal(timeRecordId));
        try {
            timeEntryList = remoteNamedJdbc.query(SqlRemoteTimeEntryQuery.SELECT_TIME_ENTRIES_BY_TIME_RECORD_ID.getSql(
                                                                                new OrderBy("DTDAY", SortOrder.ASC) ),
                                                  params, new RemoteEntryRowMapper());
        }
        catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve time entries for record {} error: {}", timeRecordId, ex.getMessage());
            throw new TimeEntryNotFoundEx("No matching TimeEntries for TimeRecord id: " + timeRecordId);
        }
        return timeEntryList;
    }

    @Override
    public void updateTimeEntry(TimeEntry timeEntry) {
        MapSqlParameterSource params = getTimeEntryParams(timeEntry);
        if (remoteNamedJdbc.update(SqlRemoteTimeEntryQuery.UPDATE_TIME_ENTRY.getSql(), params) == 0){
            remoteNamedJdbc.update(SqlRemoteTimeEntryQuery.INSERT_TIME_ENTRY.getSql(), params);
        }
    }

    private static MapSqlParameterSource getTimeEntryParams(TimeEntry timeEntry) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("tSDayId", new BigDecimal(timeEntry.getEntryId()));
        param.addValue("timesheetId", new BigDecimal(timeEntry.getTimeRecordId()));
        param.addValue("empId", timeEntry.getEmpId());
        param.addValue("employeeName", timeEntry.getEmployeeName());
        param.addValue("dayDate", toDate(timeEntry.getDate()));
        param.addValue("workHR", timeEntry.getWorkHours());
        param.addValue("travelHR", timeEntry.getTravelHours());
        param.addValue("holidayHR", timeEntry.getHolidayHours());
        param.addValue("sickEmpHR", timeEntry.getSickEmpHours());
        param.addValue("sickFamilyHR", timeEntry.getSickFamHours());
        param.addValue("miscHR", timeEntry.getMiscHours());
        param.addValue("miscTypeId", timeEntry.getMiscType() != null ?
                                        timeEntry.getMiscType().getCode() : "" );
        param.addValue("tOriginalUserId", timeEntry.getTxOriginalUserId());
        param.addValue("tUpdateUserId", timeEntry.getTxUpdateUserId());
        param.addValue("tOriginalDate", toDate(timeEntry.getTxOriginalDate()));
        param.addValue("tUpdateDate", toDate(timeEntry.getTxUpdateDate()));
        param.addValue("status", String.valueOf(getStatusCode(timeEntry.isActive())));
        param.addValue("empComment", timeEntry.getEmpComment());
        param.addValue("payType", timeEntry.getPayType().name());
        param.addValue("vacationHR", timeEntry.getVacationHours());
        param.addValue("personalHR", timeEntry.getPersonalHours());
        return param;
    }
}

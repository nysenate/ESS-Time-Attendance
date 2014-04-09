package gov.nysenate.seta.dao;


import gov.nysenate.seta.dao.mapper.RemoteEntryRowMapper;
import gov.nysenate.seta.model.MiscLeaveType;
import gov.nysenate.seta.model.PayType;
import gov.nysenate.seta.model.TimeEntry;
import gov.nysenate.seta.model.TimeRecord;
import gov.nysenate.seta.model.exception.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.exception.TimeRecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository("remoteTimeEntry")
public class SqlRemoteEntryDao extends SqlBaseDao implements TimeEntryDao{

    @Resource(name = "localTimeRecord")
    TimeRecordDao timeRecordDao;

    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteEntryDao.class);

    protected static final String GET_TIME_ENTRY_SQL_TMPL =
            "SELECT " +
                "*" +
                "FROM PD23TIMESHEET" +
                "WHERE CDSTATUS = 'A' AND %s";

    protected static final String GET_ENTRY_BY_TIMESHEETID = String.format(GET_TIME_ENTRY_SQL_TMPL,"NUXRTIMESHEET = :timesheetId");
    protected static final String GET_ENTRY_BY_EMPID = String.format(GET_TIME_ENTRY_SQL_TMPL,"NUXREFEM = :empId AND NUXRTIMESHEET = :timesheetId");

    protected static final String SET_ENTRY_SQL =
            "INSERT \n" +
                    "INTO PD23TIMESHEET (NUXRDAY, NUXRTIMESHEET, NUXREFEM, DTDAY, NUWORK, NUTRAVEL, NUHOLIDAY, NUSICKEMP, NUSICKFAM, NUMISC, NUXRMISC, NATXNORGUSER, NATXNUPDUSER, DTTXNORIGIN, DTTXNUPDATE, CDSTATUS, DECOMMENTS, CDPAYTYPE, NUVACATION, NUPERSONAL) \n" +
                    "VALUES (:tSDayId, :timesheetId, :empId, :dayDate, :workHR, :travelHR, :holidayHR, :sickEmpHR, :sickFamilyHR, :miscHR, :miscTypeId, :tOriginalUserId, :tUpdateUserId, :tOriginalDate, :tUpdateDate, :status, :empComment, :payType, :vacationHR, :personalHR)";

    protected static final String UPDATE_ENTRY_SQL =
            "UPDATE PD23TIMESHEET \n" +
                    "SET \n" +
                    "(NUXRTIMESHEET = :timesheetId, NUXREFEM = :empId, DTDAY = :dayDate, NUWORK = :workHR, NUTRAVEL = :travelHR, NUHOLIDAY = :holidayHR, NUSICKEMP = :sickEmpHR, NUSICKFAM = :sickFamilyHR, NUMISC = :miscHR, NUXRMISC = :miscTypeId, NATXNORGUSER = :tOriginalUserId, NATXNUPDUSER = :tUpdateUserId, DTTXNORIGIN = :tOriginalDate, DTTXNUPDATE = :tUpdateDate, CDSTATUS = :status, DECOMMENTS = :empComment, CDPAYTYPE = :payType, NUVACATION = :vacationHR, NUPERSONAL = :personalHR) \n" +
                    "WHERE NUXRDAY = :tSDayId";


    @Override
    public List<TimeEntry> getTimeEntryByTimesheet(int timesheetId) throws TimeEntryNotFoundEx {

        List<TimeEntry> timeEntryList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timesheetId",timesheetId);

        try{
            timeEntryList = remoteNamedJdbc.query(GET_ENTRY_BY_TIMESHEETID, params, new RemoteEntryRowMapper());
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Entries of {} error: {}", timesheetId, ex.getMessage());
            throw new TimeEntryNotFoundEx("No matching Time Entries for Timesheet id: " + timesheetId);
        }
        return  timeEntryList;

    }

    @Override
    public Map<BigDecimal, List<TimeEntry>> getTimeEntryByEmpId(int empId) throws TimeEntryNotFoundEx, TimeRecordNotFoundException {

        List<TimeRecord> timeRecords;
        Map<BigDecimal, List<TimeEntry>> timeEntryList = null;
        MapSqlParameterSource params = new MapSqlParameterSource();

        timeRecords = timeRecordDao.getRecordByEmployeeId(empId);

        for(TimeRecord tr : timeRecords)
        {
            params.addValue("empId",empId);
            params.addValue("timesheetId",tr.getTimesheetId());

            try{
                timeEntryList.put(tr.getTimesheetId(), remoteNamedJdbc.query(GET_ENTRY_BY_EMPID, params,
                        new RemoteEntryRowMapper()));
            }catch (DataRetrievalFailureException ex){
                logger.warn("Retrieve Time Entries of {} error: {}", empId, ex.getMessage());
                throw new TimeEntryNotFoundEx("No matching Time Entries for Employee id: " + empId);
            }
        }

        return timeEntryList;

    }

    @Override
    public boolean setTimeEntry(TimeEntry tsd) {

        MapSqlParameterSource param = new MapSqlParameterSource();

        param.addValue("tSDayId", tsd.gettDayId());
        param.addValue("timesheetId", tsd.getTimesheetId());
        param.addValue("empId", tsd.getEmpId());
        param.addValue("dayDate", tsd.getDate());
        param.addValue("workHR", tsd.getWorkHours());
        param.addValue("travelHR", tsd.getTravelHours());
        param.addValue("holidayHR", tsd.getHolidayHours());
        param.addValue("sickEmpHR", tsd.getSickEmpHours());
        param.addValue("sickFamilyHR", tsd.getSickFamHours());
        param.addValue("miscHR", tsd.getMiscHours());
        param.addValue("miscTypeId", (tsd.getMiscType() != null) ? tsd.getMiscType().getCode() : null);
        param.addValue("tOriginalUserId", tsd.gettOriginalUserId());
        param.addValue("tUpdateUserId", tsd.gettUpdateUserId());
        param.addValue("tOriginalDate", tsd.gettOriginalDate());
        param.addValue("tUpdateDate", tsd.gettUpdateDate());
        param.addValue("status", getStatusCode(tsd.isActive()));
        param.addValue("empComment", tsd.getEmpComment());
        param.addValue("payType", tsd.getPayType().name());
        param.addValue("vacationHR", tsd.getVacationHours());
        param.addValue("personalHR", tsd.getPersonalHours());

        if(remoteNamedJdbc.update(SET_ENTRY_SQL, param)==1) return true;
        else return false;
    }

    @Override
    public boolean updateTimeEntry(TimeEntry tsd) {

        MapSqlParameterSource param = new MapSqlParameterSource();

        param.addValue("tSDayId", tsd.gettDayId());
        param.addValue("timesheetId", tsd.getTimesheetId());
        param.addValue("empId", tsd.getEmpId());
        param.addValue("dayDate", tsd.getDate());
        param.addValue("workHR", tsd.getWorkHours());
        param.addValue("travelHR", tsd.getTravelHours());
        param.addValue("holidayHR", tsd.getHolidayHours());
        param.addValue("sickEmpHR", tsd.getSickEmpHours());
        param.addValue("sickFamilyHR", tsd.getSickFamHours());
        param.addValue("miscHR", tsd.getMiscHours());
        param.addValue("miscTypeId", tsd.getMiscType().getCode());
        param.addValue("tOriginalUserId", tsd.gettOriginalUserId());
        param.addValue("tUpdateUserId", tsd.gettUpdateUserId());
        param.addValue("tOriginalDate", tsd.gettOriginalDate());
        param.addValue("tUpdateDate", tsd.gettUpdateDate());
        param.addValue("status", getStatusCode(tsd.isActive()));
        param.addValue("empComment", tsd.getEmpComment());
        param.addValue("payType", tsd.getPayType().name());
        param.addValue("vacationHR", tsd.getVacationHours());
        param.addValue("personalHR", tsd.getPersonalHours());

        if(remoteNamedJdbc.update(UPDATE_ENTRY_SQL, param)==1) return true;
        else return false;
    }
}

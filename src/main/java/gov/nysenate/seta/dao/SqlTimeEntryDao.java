package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class SqlTimeEntryDao extends SqlBaseDao implements TimeEntryDao{

    @Resource(name = "localTimeRecord")
    private TimeRecordDao timeRecordDao;

    private static final Logger logger = LoggerFactory.getLogger(SqlTimeEntryDao.class);

    protected static final String GET_TIME_ENTRY_SQL_TMPL =
        "SELECT \n" +
            "tsd.* \n" +
            "FROM TimesheetByDay tsd\n" +
            "Where tsd.Status = 'A' AND %s";

    protected static final String GET_ENTRY_BY_TIMESHEETID = String.format(GET_TIME_ENTRY_SQL_TMPL,"tsd.TimesheetId = :timesheetId");
    protected static final String GET_ENTRY_BY_EMPID = String.format(GET_TIME_ENTRY_SQL_TMPL,"tsd.EmpId = :empId AND tsd.TimesheetId = :timesheetId");

    protected static final String SET_ENTRY_SQL =
        "INSERT \n" +
        "INTO (TSDayId, TimesheetId, EmpId, DayDate, WorkHR, TravelHR, HolidayHR, SickEmpHR, SickFamilyHR, MiscHR, MiscTypeId, TOriginalUserId, TUpdateUserId, TOriginalDate, TUpdateDate, Status, EmpComment, PayType, VacationHR, PersonalHR) \n" +
        "VALUES (:tSDayId, :timesheetId, :empId, :dayDate, :workHR, :travelHR, :holidayHR, :sickEmpHR, :sickFamilyHR, :miscHR, :miscTypeId, :tOriginalUserId, :tUpdateUserId, :tOriginalDate, :tUpdateDate, :status, :empComment, :payType, :vacationHR, :personalHR)";

    protected static final String UPDATE_ENTRY_SQL =
        "UPDATE TimesheetByDay \n" +
        "SET \n" +
        "(TimesheetId = :timesheetId, EmpId = :empId, DayDate = :dayDate, WorkHR = :workHR, TravelHR = :travelHR, HolidayHR = :holidayHR, SickEmpHR = :sickEmpHR, SickFamilyHR = :sickFamilyHR, MiscHR = :miscHR, MiscTypeId = :miscTypeId, TOriginalUserId = :tOriginalUserId, TUpdateUserId = :tUpdateUserId, TOriginalDate = :tOriginalDate, TUpdateDate = :tUpdateDate, Status = :status, EmpComment = :empComment, PayType = :payType, VacationHR = :vacationHR, PersonalHR = :personalHR) \n" +
        "WHERE TSDayId = :tSDayId";

    @Override
    public List<TimeEntry> getTimeEntryByTimesheet(int timesheetId) throws TimeEntryNotFoundEx {

        List<TimeEntry> timeEntryList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timesheetId",timesheetId);

        try{
            timeEntryList = localNamedJdbc.query(GET_ENTRY_BY_TIMESHEETID, params,
                    new TimeEntryRowMapper(""));
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Entries of {} error: {}", timesheetId, ex.getMessage());
            throw new TimeEntryNotFoundEx("No matching Time Entries for Timesheet id: " + timesheetId);
        }
        return  timeEntryList;

    }

    @Override
    public Map<Integer, List<TimeEntry>> getTimeEntryByEmpId(int empId) throws TimeEntryNotFoundEx, TimeRecordNotFoundException{

        List<TimeRecord> timeRecords;
        Map<Integer, List<TimeEntry>> timeEntryList = null;
        MapSqlParameterSource params = new MapSqlParameterSource();

        timeRecords = timeRecordDao.getRecordByEmployeeId(empId);

        for(TimeRecord tr : timeRecords)
        {
            params.addValue("empId",empId);
            params.addValue("timesheetId",tr.getTimesheetId());

            try{
                timeEntryList.put(tr.getTimesheetId(), localNamedJdbc.query(GET_ENTRY_BY_EMPID, params,
                        new TimeEntryRowMapper("")));
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

        if(localNamedJdbc.update(SET_ENTRY_SQL, param)==1) return true;
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

        if(localNamedJdbc.update(UPDATE_ENTRY_SQL, param)==1) return true;
        else return false;
    }
}

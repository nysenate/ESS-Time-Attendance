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
}

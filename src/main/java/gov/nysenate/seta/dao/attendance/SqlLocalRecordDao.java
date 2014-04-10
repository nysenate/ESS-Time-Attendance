package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.exception.TimeRecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository("localTimeRecord")
public class SqlLocalRecordDao extends SqlBaseDao implements TimeRecordDao{

    private static final Logger logger = LoggerFactory.getLogger(SqlLocalRecordDao.class);

    protected static final String GET_TIME_REC_SQL_TMPL =
        "SELECT * " +
                "FROM " +
                "ts.time_record" +
                " WHERE status = 'A' AND %s ";

    protected static final String GET_TREC_BY_EMPID_SQL = String.format(GET_TIME_REC_SQL_TMPL, "emp_id = :empId");
    protected static final String GET_TREC_BY_DATE_SQL = String.format(GET_TIME_REC_SQL_TMPL, "date_begin = :startDate AND date_end = :endDate");
    protected static final String GET_TREC_BY_RECSTATUS_SQL = String.format(GET_TIME_REC_SQL_TMPL, "ts_status_id = :tSStatusId AND emp_id = :empId AND date_begin = :startDate AND date_end = :endDate");
    protected static final String GET_TIME_RECORD_COUNT_SQL = "SELECT COUNT(*) FROM ts.time_record WHERE time_record_id = :time_record_id";

    protected static final String SET_TIME_REC_SQL =
        "INSERT \n" +
            "INTO ts.time_record \n" +
            "(time_record_id , emp_id, t_original_user, t_update_user, t_original_date, t_update_date, status, ts_status_id, begin_date, end_date, remarks, supervisor_id, exc_details, proc_date) \n" +
            "VALUES (:timesheetId, :empId, :tOriginalUserId, :tUpdateUserId, :tOriginalDate, :tUpdateDate, :status, :tSStatusId, :beginDate, :endDate, :remarks, :supervisorId, :excDetails, :procDate) \n";

    protected static final String UPDATE_TIME_REC_SQL =
        "UPDATE ts.time_record " +
            "SET " +
            "emp_id = :empId, t_original_user = :tOriginalUserId, t_update_user = :tUpdateUserId, t_original_date = :tOriginalDate, t_update_date = :tUpdateDate, status = :status, ts_status_id = :tSStatusId, begin_date = :beginDate, end_date = :endDate, remarks = :remarks, supervisor_id = :supervisorId, exc_details = :excDetails, proc_date = :procDate " +
            "WHERE time_record_id = :timesheetId";


    @Override
    public List<TimeRecord> getRecordByEmployeeId(int empId) throws TimeRecordNotFoundException {

        List<TimeRecord> timeRecordList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId",empId);

        try{
            timeRecordList = localNamedJdbc.query(GET_TREC_BY_EMPID_SQL, params,
                                            new LocalRecordRowMapper(""));
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records of {} error: {}", empId, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for employee id: " + empId);
        }
        return  timeRecordList;

    }

    @Override
    public Map<Integer, List<TimeRecord>> getRecordByEmployeeIdMap(List<Integer> empIds) throws TimeRecordNotFoundException {

        Map<Integer, List<TimeRecord>> trs = null;
        MapSqlParameterSource params = new MapSqlParameterSource();

        for(Integer empId : empIds)
        {
            params.addValue("empId",empId);

            try{

                 trs.put(empId, localNamedJdbc.query(GET_TREC_BY_EMPID_SQL, params, new LocalRecordRowMapper("")));

            }catch (DataRetrievalFailureException ex){
                logger.warn("Retrieve Time Records of {} error: {}", empId, ex.getMessage());
                throw new TimeRecordNotFoundException("No matching Time Records for employee id: " + empId);
            }

        }

        return  trs;
    }

    @Override
    public List<TimeRecord> getRecordByPayPeriod(Date startDate, Date endDate) throws TimeRecordNotFoundException {

        List<TimeRecord> timeRecordList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate",startDate);
        params.addValue("endDate", endDate);

        try{
            timeRecordList = localNamedJdbc.query(GET_TREC_BY_DATE_SQL, params,
                    new LocalRecordRowMapper(""));
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records between Dates {} And {} error: {}", startDate, endDate, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records between Dates: " + startDate +"  And  "+ endDate);
        }
        return  timeRecordList;

    }

    @Override
    public List<TimeRecord> getRecordByTSStatus(String tSStatusId, int empId, Date startDate, Date endDate) throws TimeRecordNotFoundException {

        List<TimeRecord> timeRecordList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tSStatusId",tSStatusId);

        try{
            timeRecordList = localNamedJdbc.query(GET_TREC_BY_RECSTATUS_SQL, params,
                    new LocalRecordRowMapper(""));
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records where Status : {} error: {}", tSStatusId, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for Status Id : " + tSStatusId);
        }
        return  timeRecordList;
    }


    public int getTimeRecordCount(BigDecimal time_record_id) throws TimeRecordNotFoundException {

        int count = 0 ;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("time_record_id",time_record_id);

        try{
            count = localNamedJdbc.queryForObject(GET_TIME_RECORD_COUNT_SQL, params, Integer.class);
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Record Count : {} error: {}", time_record_id, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for time record Id : " + time_record_id);
        }
        return  count;
    }

    @Override
    public boolean setRecord(TimeRecord tr) {

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("timesheetId", tr.getTimesheetId());
        params.addValue("empId", tr.getEmployeeId());
        params.addValue("tOriginalUserId", tr.gettOriginalUserId());
        params.addValue("tUpdateUserId", tr.gettUpdateUserId());
        params.addValue("tOriginalDate", tr.gettOriginalDate());
        params.addValue("tUpdateDate", tr.gettUpdateDate());
        if(tr.isActive()==true){params.addValue("status", "A");}
        else{ params.addValue("status", "I");}
        params.addValue("tSStatusId", tr.getRecordStatus().getCode());
        params.addValue("beginDate", tr.getBeginDate());
        params.addValue("endDate", tr.getEndDate());
        params.addValue("remarks", tr.getRemarks());
        params.addValue("supervisorId", tr.getSupervisorId());
        params.addValue("excDetails", tr.getExeDetails());
        params.addValue("procDate", tr.getProDate());

        if (localNamedJdbc.update(SET_TIME_REC_SQL, params)==1) {    return true;}
        else{   return false;}

    }

    @Override
    public boolean updateRecord(TimeRecord tr) {

        MapSqlParameterSource params = new MapSqlParameterSource();


        params.addValue("empId", tr.getEmployeeId());
        params.addValue("tOriginalUserId", tr.gettOriginalUserId());
        params.addValue("tUpdateUserId", tr.gettUpdateUserId());
        params.addValue("tOriginalDate", tr.gettOriginalDate());
        params.addValue("tUpdateDate", tr.gettUpdateDate());
        if(tr.isActive()==true){params.addValue("status", "A");}
        else{ params.addValue("status", "I");}
        params.addValue("tSStatusId", tr.getRecordStatus().getCode());
        params.addValue("beginDate", tr.getBeginDate());
        params.addValue("endDate", tr.getEndDate());
        params.addValue("remarks", tr.getRemarks());
        params.addValue("supervisorId", tr.getSupervisorId());
        params.addValue("excDetails", tr.getExeDetails());
        params.addValue("procDate", tr.getProDate());
        params.addValue("timesheetId", tr.getTimesheetId());

        if (localNamedJdbc.update(UPDATE_TIME_REC_SQL, params)==1) return true;
        else return false;

    }


}

package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository("remoteTimeRecord")
public class SqlRemoteRecordDao extends SqlBaseDao implements TimeRecordDao{

    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteRecordDao.class);

    protected static final String GET_TIME_REC_SQL_TMPL =
            "SELECT * " +
                    "FROM " +
                    "PM23TIMESHEET" +
                    "WHERE CDSTATUS = 'A' AND %s ";

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


    @Override
    public List<TimeRecord> getRecordByEmployeeId(int empId) throws TimeRecordNotFoundException {

        List<TimeRecord> timeRecordList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId",empId);

        try{
            timeRecordList = remoteNamedJdbc.query(GET_TREC_BY_EMPID_SQL, params,
                    new RemoteRecordRowMapper());
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

                trs.put(empId, remoteNamedJdbc.query(GET_TREC_BY_EMPID_SQL, params, new RemoteRecordRowMapper()));

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
            timeRecordList = remoteNamedJdbc.query(GET_TREC_BY_DATE_SQL, params,
                    new RemoteRecordRowMapper());
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
            timeRecordList = remoteNamedJdbc.query(GET_TREC_BY_RECSTATUS_SQL, params,
                    new RemoteRecordRowMapper());
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records where Status : {} error: {}", tSStatusId, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for Status Id : " + tSStatusId);
        }
        return  timeRecordList;
    }

    @Override
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
    public boolean updateRecord(TimeRecord tr) {

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("timesheetId", tr.getTimeRecordId());
        params.addValue("empId", tr.getEmployeeId());
        params.addValue("tOriginalUserId", tr.getTxOriginalUserId());
        params.addValue("tUpdateUserId", tr.getTxUpdateUserId());
        params.addValue("tOriginalDate", tr.getTxOriginalDate());
        params.addValue("tUpdateDate", tr.getTxUpdateDate());
        params.addValue("status", getStatusCode(tr.isActive()));
        params.addValue("tSStatusId", tr.getRecordStatus().getCode());
        params.addValue("beginDate", tr.getBeginDate());
        params.addValue("endDate", tr.getEndDate());
        params.addValue("remarks", tr.getRemarks());
        params.addValue("supervisorId", tr.getSupervisorId());
        params.addValue("excDetails", tr.getExceptionDetails());
        params.addValue("procDate", tr.getProcessedDate());

        if (remoteNamedJdbc.update(UPDATE_TIME_REC_SQL, params)==1) return true;
        else return false;

    }

    @Override
    public int getTimeRecordCount(BigDecimal timesheetId) throws TimeRecordNotFoundException {
        return 0;
    }

}




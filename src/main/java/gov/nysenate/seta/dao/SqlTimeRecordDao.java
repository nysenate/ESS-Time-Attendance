package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TimeRecord;
import gov.nysenate.seta.model.TimeRecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository("localTimeRecord")
public class SqlTimeRecordDao extends SqlBaseDao implements TimeRecordDao{

    private static final Logger logger = LoggerFactory.getLogger(SqlTimeRecordDao.class);

    protected static final String GET_TIME_REC_SQL_TMPL =
        "SELECT \n" +
            "ts.*, \n" +
            "FROM \n" +
            "Timesheet ts \n" +
            "WHERE ts.Status = 'A' AND %s \n";

    protected static final String GET_TREC_BY_EMPID_SQL = String.format(GET_TIME_REC_SQL_TMPL, "ts.EmpId = :empId");
    protected static final String GET_TREC_BY_DATE_SQL = String.format(GET_TIME_REC_SQL_TMPL, "ts.DateBegin = :startDate AND ts.DateEnd = :endDate");
    protected static final String GET_TREC_BY_RECSTATUS_SQL = String.format(GET_TIME_REC_SQL_TMPL, "ts.TSStatusId = :tSStatusId AND ts.EmpId = :empId AND ts.DateBegin = :startDate AND ts.DateEnd = :endDate");

    @Override
    public List<TimeRecord> getRecordByEmployeeId(int empId) throws TimeRecordNotFoundException {

        List<TimeRecord> timeRecordList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId",empId);

        try{
            timeRecordList = localNamedJdbc.query(GET_TREC_BY_EMPID_SQL, params,
                                            new TimeRecordRowMapper(""));
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

                 trs.put(empId, localNamedJdbc.query(GET_TREC_BY_EMPID_SQL, params, new TimeRecordRowMapper("")));

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
                    new TimeRecordRowMapper(""));
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
                    new TimeRecordRowMapper(""));
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records where Status : {} error: {}", tSStatusId, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for Status Id : " + tSStatusId);
        }
        return  timeRecordList;
    }

    @Override
    public Boolean AddTimeRecord() {
        return null;
    }
}

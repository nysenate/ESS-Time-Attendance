package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;
import gov.nysenate.seta.model.exception.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.exception.TimeRecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SqlMigrationDao extends SqlBaseDao implements MigrationDao {

    @Resource
    private TimeRecordDao timeRecordDao;

    @Resource
    private TimeEntryDao timeEntryDao;


    private static final Logger logger = LoggerFactory.getLogger(SqlMigrationDao.class);

    public static final String GET_REMOTE_TIME_RECORD =
            "SELECT * " +
                "FROM " +
                    "(SELECT rownum as RN, t1.* " +
                         "FROM " +
                             "(SELECT t.* " +
                                  "FROM " +
                                      "PM23TIMESHEET t ORDER BY t.DTTXNORIGIN " +
                             ") t1 " +
                    ") "+
                "WHERE RN > :rowNumber AND RN <= :threshold";

    public static final String GET_REMOTE_TIME_ENTRY =
            "SELECT * " +
                "FROM " +
                    "(SELECT rownum as RN, t1.* " +
                         "FROM " +
                             "(SELECT t.* " +
                                  "FROM " +
                                      "PD23TIMESHEET t ORDER BY t.DTTXNORIGIN " +
                             ") t1 " +
                    ") "+
                "WHERE RN > :rowNumber AND RN <= :threshold";


    @Override
    public List<TimeRecord> getRemoteTimeRecord(int rowNumber, int threshold) throws TimeRecordNotFoundException {

        List<TimeRecord> timeRecordList = null;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rowNumber", rowNumber);
        params.addValue("threshold", threshold);

        try {
            timeRecordList = remoteNamedJdbc.query(GET_REMOTE_TIME_RECORD, params, new RowMapper<TimeRecord>() {
                @Override
                public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TimeRecord tr = new TimeRecord();
                    tr.setTimesheetId(rs.getBigDecimal("NUXRTIMESHEET"));
                    tr.setEmployeeId(rs.getBigDecimal("NUXREFEM"));
                    tr.settOriginalUserId(rs.getString("NATXNORGUSER"));
                    tr.settUpdateUserId(rs.getString("NATXNUPDUSER"));
                    tr.settOriginalDate(rs.getTimestamp("DTTXNORIGIN"));
                    tr.settUpdateDate(rs.getTimestamp("DTTXNUPDATE"));
                    tr.setActive(rs.getString("CDSTATUS").equals("A"));
                    tr.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString("CDTSSTAT")));
                    tr.setBeginDate(rs.getDate("DTBEGIN"));
                    tr.setEndDate(rs.getDate("DTEND"));
                    tr.setRemarks(rs.getString("DEREMARKS"));
                    tr.setSupervisorId(rs.getBigDecimal("NUXREFSV"));
                    tr.setExeDetails(rs.getString("DEEXCEPTION"));
                    tr.setProDate(rs.getDate("DTPROCESS"));

                    return tr;
                }
            });
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records where rownumber : {} error: {}", rowNumber, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for Row Number Range : " + rowNumber + "-" + threshold);
        }

        return timeRecordList;
    }

    @Override
    public List<TimeEntry> getRemoteTimeEntry(int rowNumber, int threshold) throws TimeEntryNotFoundEx {

        List<TimeEntry> timeEntryList = null;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rowNumber", rowNumber);
        params.addValue("threshold", threshold);

        try {
            timeEntryList = remoteNamedJdbc.query(GET_REMOTE_TIME_ENTRY, params, new RowMapper<TimeEntry>() {
                @Override
                public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TimeEntry te = new TimeEntry();
                    te.settDayId(rs.getBigDecimal("NUXRDAY"));
                    te.setTimesheetId(rs.getBigDecimal("NUXRTIMESHEET"));
                    te.setEmpId(rs.getBigDecimal("NUXREFEM"));
                    te.setDate(rs.getDate("DTDay"));
                    te.setWorkHours(rs.getBigDecimal("NUWORK"));
                    te.setTravelHours(rs.getBigDecimal("NUTRAVEL"));
                    te.setHolidayHours(rs.getBigDecimal("NUHOLIDAY"));
                    te.setVacationHours(rs.getBigDecimal("NUVACATION"));
                    te.setPersonalHours(rs.getBigDecimal("NUPERSONAL"));
                    te.setSickEmpHours(rs.getBigDecimal("NUSICKEMP"));
                    te.setSickFamHours(rs.getBigDecimal("NUSICKFAM"));
                    te.setMiscHours(rs.getBigDecimal("NUMISC"));
                    if (rs.getString("NUXRMISC") != null) te.setMiscType(MiscLeaveType.valueOf(rs.getString("NUXRMISC")));
                    te.settOriginalUserId(rs.getString("NATXNORGUSER"));
                    te.settUpdateUserId(rs.getString("NATXNUPDUSER"));
                    te.settOriginalDate(rs.getTimestamp("DTTXNORIGIN"));
                    te.settUpdateDate(rs.getTimestamp("DTTXNUPDATE"));
                    te.setActive(rs.getString("CDSTATUS").equals("A"));
                    te.setEmpComment(rs.getString("DECOMMENTS"));
                    te.setPayType(PayType.valueOf(rs.getString("CDPAYTYPE")));

                    return te;
                }
            });

        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Entries where rownumber : {} error: {}", rowNumber, ex.getMessage());
            throw new TimeEntryNotFoundEx("No matching Time Records for Row Number Range : " + rowNumber + "-" + threshold);
        }

        return timeEntryList;
    }

    @Override
    public void MigrateTimeRecord() throws TimeRecordNotFoundException {

        int threshold = 1000 ;
        int rowNum = 0 ;
        List<TimeRecord> timeRecordList;

        while(true)
        {
            timeRecordList=getRemoteTimeRecord(rowNum,rowNum+threshold);

            for(TimeRecord tr : timeRecordList)
            {
                timeRecordDao.setRecord(tr);
            }

            if(timeRecordList.size()==threshold)
            {

                rowNum = rowNum + threshold;
                break;
            }
            else
            {
                break;
            }
        }

    }

    @Override
    public void MigrateTimeEntry() throws TimeEntryNotFoundEx{

        int threshold = 1000 ;
        int rowNum = 0 ;
        List<TimeEntry> timeEntryList;

        while(true)
        {
            timeEntryList=getRemoteTimeEntry(rowNum,rowNum+threshold);

            for(TimeEntry te : timeEntryList)
            {
                timeEntryDao.setTimeEntry(te);
            }

            if(timeEntryList.size()==threshold)
            {
                rowNum = rowNum + threshold;
            }
            else
            {
                break;
            }
        }
    }
}

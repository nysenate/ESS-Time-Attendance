package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.attendance.mapper.RemoteEntryRowMapper;
import gov.nysenate.seta.dao.attendance.mapper.RemoteRecordRowMapper;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.attendance.SyncCheck;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordAudit;
import gov.nysenate.seta.model.attendance.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.attendance.TimeRecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Resource(name = "localTimeRecordDao")
    private TimeRecordDao localRecordDao;

    @Resource(name = "localTimeEntry")
    private TimeEntryDao localEntryDao;


    @Autowired
    private SyncCheckDao syncCheckDao;


    private static final Logger logger = LoggerFactory.getLogger(SqlMigrationDao.class);

    public static final String GET_REMOTE_TIME_RECORD_AUDIT =
            "SELECT * " +
                "FROM " +
                    "( SELECT rownum as RN, t1.* " +
                         "FROM " +
                             "( SELECT x1.NUXRTSAUD, x1.DTTSINSAUD, x1.NATSINSAUD, x2.* " +
                                  "FROM TS_OWNER.PM23TIMESHTAUD x1 " +
                                    "RIGHT JOIN TS_OWNER.PM23TIMESHEET x2 ON x1.NUXRTIMESHEET = x2.NUXRTIMESHEET " +
                                      "ORDER BY x1.DTTXNORIGIN, x2.DTTXNUPDATE " +
                             ") t1 " +
                    ") "+
                "WHERE RN > :rowNumber AND RN <= :threshold";

    public static final String GET_REMOTE_TIME_ENTRY =
            "SELECT * " +
                "FROM " +
                    "(SELECT rownum as RN, t1.* " +
                         "FROM " +
                             "(SELECT t.* , m.CDMISCLV " +
                                  "FROM " +
                                        "PD23TIMESHEET t " +
                                            "LEFT JOIN PL23MISCLV m ON t.NUXRMISC = m.NUXRMISC " +
                                                "ORDER BY t.DTTXNORIGIN " +
                             ") t1 " +
                    ") "+
                "WHERE RN > :rowNumber AND RN <= :threshold";


    @Override
    public List<TimeRecordAudit> getRemoteTimeRecordAudit(int rowNumber, int threshold) throws TimeRecordNotFoundException {

        List<TimeRecordAudit> timeRecordAuditList = null;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rowNumber", rowNumber);
        params.addValue("threshold", threshold);

        try {
            timeRecordAuditList = remoteNamedJdbc.query(GET_REMOTE_TIME_RECORD_AUDIT, params, new RowMapper<TimeRecordAudit>() {
                @Override
                public TimeRecordAudit mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TimeRecordAudit rau = new TimeRecordAudit();
                    RemoteRecordRowMapper remoteRecordRowMapper = new RemoteRecordRowMapper();
                    rau.setAuditName(rs.getString("NATSINSAUD"));
                    rau.setAuditDate(rs.getTimestamp("DTTSINSAUD"));
                    rau.setAuditId(rs.getBigDecimal("NUXRTSAUD"));
                    rau.setTimeRecord(remoteRecordRowMapper.mapRow(rs, rowNum));

                    return rau;
                }
            });
        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Records where rownumber : {} error: {}", rowNumber, ex.getMessage());
            throw new TimeRecordNotFoundException("No matching Time Records for Row Number Range : " + rowNumber + "-" + threshold);
        }

        return timeRecordAuditList;
    }

    @Override
    public List<TimeEntry> getRemoteTimeEntry(int rowNumber, int threshold) throws TimeEntryNotFoundEx {

        List<TimeEntry> timeEntryList = null;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rowNumber", rowNumber);
        params.addValue("threshold", threshold);

        try {
            timeEntryList = remoteNamedJdbc.query(GET_REMOTE_TIME_ENTRY, params, new RemoteEntryRowMapper());

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
        List<TimeRecordAudit> timeRecordAuditList;

        while(true)
        {
            timeRecordAuditList=getRemoteTimeRecordAudit(rowNum, rowNum + threshold);

            for(TimeRecordAudit rau : timeRecordAuditList)
            {
                TimeRecord tr = rau.getTimeRecord();

                if(timeRecordAuditList.indexOf(rau) == (timeRecordAuditList.size()-1))
                {
                    SyncCheck sc = new SyncCheck();
                    sc.setDate(rau.getAuditDate());
                    sc.setDataId(rau.getAuditId());
                    sc.setDataType("TimeRecord");
                    sc.setDataSide("Remote");

                    syncCheckDao.setSyncData(sc);
                }

              //  if(localRecordDao.getTimeRecordCount(tr.getTimeRecordId())==0)
              //  {
//                    localRecordDao.setRecord(tr);
//                }
//                else
//                {
//                    localRecordDao.saveRecord(tr);
//                }


            }

            if(timeRecordAuditList.size()==threshold)
            {

                rowNum = rowNum + threshold;

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
            timeEntryList=getRemoteTimeEntry(rowNum, rowNum + threshold);

            for(TimeEntry te : timeEntryList)
            {
                if(timeEntryList.indexOf(te) == (timeEntryList.size()-1))
                {
                    SyncCheck sc = new SyncCheck();
                    sc.setDate(te.gettOriginalDate());
                    sc.setDataId(te.getEntryId());
                    sc.setDataType("TimeEntry");
                    sc.setDataSide("Remote");

                    syncCheckDao.setSyncData(sc);
                }

                localEntryDao.setTimeEntry(te);
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

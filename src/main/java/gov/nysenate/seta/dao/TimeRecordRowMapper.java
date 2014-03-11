package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.PayType;
import gov.nysenate.seta.model.TimeRecord;
import gov.nysenate.seta.model.TimeRecordStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by riken on 3/4/14.
 */
public class TimeRecordRowMapper implements RowMapper<TimeRecord>
{
    private String pfx="";

    public TimeRecordRowMapper(String pfx){
        this.pfx = pfx;
    }

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord trec = new TimeRecord();
        trec.setTimesheetId(rs.getInt(pfx + "TimesheetId"));
        trec.setEmployeeId(rs.getInt(pfx + "EmpId"));
        trec.settOriginalUserId(rs.getInt(pfx + "TOriginalUserId"));
        trec.settUpdateUserId(rs.getInt(pfx + "TUpdateUserId"));
        trec.settOriginalDate(rs.getTimestamp(pfx + "TOriginalDate"));
        trec.settUpdateDate(rs.getTimestamp(pfx + "TUpdateDate"));
        trec.setActive(rs.getString(pfx + "Status").equals("A"));
        trec.setRecordStatus(TimeRecordStatus.valueOf(rs.getString(pfx + "TSStatusId")));
        trec.setBeginDate(rs.getDate(pfx + "BeginDate"));
        trec.setEndDate(rs.getDate(pfx + "EndDate"));
        trec.setPayType(PayType.valueOf(rs.getString(pfx + "PayType")));
        trec.setRemarks(rs.getString(pfx + "Remarks"));
        trec.setExeDetails(rs.getString(pfx + "ExcDetails"));
        trec.setProDate(rs.getDate(pfx + "ProcDate"));

        return trec;
    }
}

package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.accrual.mapper.LocalAccrualUsageRowMapper;
import gov.nysenate.seta.dao.attendance.mapper.LocalEntryRowMapper;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.accrual.AccrualUsage;
import gov.nysenate.seta.model.accrual.PeriodAccUsage;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeEntryException;
import gov.nysenate.seta.model.attendance.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.period.PayPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

@Repository("localTimeEntry")
public class SqlLocalEntryDao extends SqlBaseDao implements TimeEntryDao{

    @Resource(name = "localTimeRecordDao")
    private TimeRecordDao timeRecordDao;

    private static final Logger logger = LoggerFactory.getLogger(SqlLocalEntryDao.class);

    protected static final String GET_TIME_ENTRY_SQL_TMPL =
        "SELECT *\n" +
        "FROM ts.time_entry \n" +
        "WHERE status = 'A' AND %s";

    protected static final String GET_ENTRY_BY_TIME_RECORD_ID = String.format(GET_TIME_ENTRY_SQL_TMPL,"time_record_id = :timeRecordId");
    protected static final String GET_ENTRY_BY_EMP_ID = String.format(GET_TIME_ENTRY_SQL_TMPL,"emp_id = :empId AND timesheet_id = :timeRecordId");

    protected static final String SET_ENTRY_SQL =
        "INSERT \n" +
        "INTO ts.time_entry (time_entry_id, time_record_id, emp_id, day_date, work_hr, travel_hr, holiday_hr, " +
        "                    sick_emp_hr, sick_family_hr, misc_hr, misc_type, t_original_user, t_update_user, " +
        "                    t_original_date, t_update_date, status, emp_comment, pay_type, vacation_hr, personal_hr) \n" +
        "VALUES (:tSDayId, :timeRecordId, :empId, :dayDate, :workHR, :travelHR, :holidayHR, :sickEmpHR, :sickFamilyHR, :miscHR, :miscTypeId, :tOriginalUserId, :tUpdateUserId, :tOriginalDate, :tUpdateDate, :status, :empComment, :payType, :vacationHR, :personalHR)";

    protected static final String UPDATE_ENTRY_SQL =
        "UPDATE ts.time_entry \n" +
        "SET \n" +
        "(time_record_id = :timeRecordId, emp_id = :empId, day_date = :dayDate, work_hr = :workHR, travel_hr = :travelHR, holiday_hr = :holidayHR, sick_emp_hr = :sickEmpHR, sick_family_hr = :sickFamilyHR, misc_hr = :miscHR, misc_type = :miscTypeId, t_original_user = :tOriginalUserId, t_update_user = :tUpdateUserId, t_original_date = :tOriginalDate, t_update_date = :tUpdateDate, status = :status, emp_comment = :empComment, pay_type = :payType, vacation_hr = :vacationHR, personal_hr = :personalHR) \n" +
        "WHERE tsDayId = :tSDayId";

    protected static final String GET_SUM_OF_HOURS =
        "SELECT " +
            "te.emp_id as t_emp_id, " +
            "SUM(te.work_hr) as t_work, " +
            "SUM(te.travel_hr) as t_travel, " +
            "SUM(te.holiday_hr) as t_holiday, " +
            "SUM(te.sick_emp_hr) as t_sick_emp, " +
            "SUM(te.sick_family_hr) as t_sick_family, " +
            "SUM(te.misc_hr) as t_misc, " +
            "SUM(te.vacation_hr) as t_vacation, " +
            "SUM(te.personal_hr) as t_personal " +
        "FROM " +
            "ts.time_entry te LEFT JOIN ts.time_record tr ON te.time_record_id = tr.time_record_id " +
        "WHERE " +
            "te.emp_id = :empId AND " +
            "te.day_date BETWEEN  :startDate AND :endDate" +
        "GROUP BY te.emp_id";

    @Override
    public TimeEntry getTimeEntryById(BigInteger timeEntryId) throws TimeEntryException {
        return null;
    }

    //@Override
    public List<TimeEntry> getTimeEntriesByRecordId(BigInteger timeRecordId) throws TimeEntryNotFoundEx {

        List<TimeEntry> timeEntryList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timesheetId", timeRecordId);
        try {
            timeEntryList = localNamedJdbc.query(GET_ENTRY_BY_TIME_RECORD_ID, params, new LocalEntryRowMapper(""));
        }
        catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Time Entries of {} error: {}", timeRecordId, ex.getMessage());
            throw new TimeEntryNotFoundEx("No matching Time Entries for Timesheet id: " + timeRecordId);
        }
        return  timeEntryList;

    }

//    @Override
//    public Map<String, List<TimeEntry>> getTimeEntryByEmpId(int empId) throws TimeEntryNotFoundEx, TimeRecordNotFoundException {
//
//        List<TimeRecord> timeRecords;
//        Map<String, List<TimeEntry>> timeEntryList = null;
//        MapSqlParameterSource params = new MapSqlParameterSource();
//
//        timeRecords = new ArrayList<>(); /**TODO FIX THIS *///timeRecordDao.getRecordByEmployeeId(empId);
//
//        for(TimeRecord tr : timeRecords)
//        {
//            params.addValue("empId",empId);
//            params.addValue("timesheetId",tr.getTimeRecordId());
//
//            try{
//                timeEntryList.put(tr.getTimeRecordId(), localNamedJdbc.query(GET_ENTRY_BY_EMP_ID, params,
//                        new LocalEntryRowMapper("")));
//            }catch (DataRetrievalFailureException ex){
//                logger.warn("Retrieve Time Entries of {} error: {}", empId, ex.getMessage());
//                throw new TimeEntryNotFoundEx("No matching Time Entries for Employee id: " + empId);
//            }
//        }
//
//        return timeEntryList;
//    }

    //@Override TODO CLEAN UP
    public boolean setTimeEntry(TimeEntry tsd) {

        MapSqlParameterSource param = new MapSqlParameterSource();

        param.addValue("tSDayId", tsd.getEntryId());
        param.addValue("timesheetId", tsd.getTimeRecordId());
        param.addValue("empId", tsd.getEmpId());
        param.addValue("dayDate", tsd.getDate());
        param.addValue("workHR", tsd.getWorkHours());
        param.addValue("travelHR", tsd.getTravelHours());
        param.addValue("holidayHR", tsd.getHolidayHours());
        param.addValue("sickEmpHR", tsd.getSickEmpHours());
        param.addValue("sickFamilyHR", tsd.getSickFamHours());
        param.addValue("miscHR", tsd.getMiscHours());
        param.addValue("miscTypeId", tsd.getMiscType()!= null ? tsd.getMiscType().getCode():null);
        param.addValue("tOriginalUserId", tsd.getTxOriginalUserId());
        param.addValue("tUpdateUserId", tsd.getTxUpdateUserId());
        param.addValue("tOriginalDate", tsd.getTxOriginalDate());
        param.addValue("tUpdateDate", tsd.getTxUpdateDate());
        param.addValue("status", getStatusCode(tsd.isActive()));
        param.addValue("empComment", tsd.getEmpComment());
        param.addValue("payType", tsd.getPayType().name());
        param.addValue("vacationHR", tsd.getVacationHours());
        param.addValue("personalHR", tsd.getPersonalHours());

        if(localNamedJdbc.update(SET_ENTRY_SQL, param)==1) return true;
        else return false;

    }

    //@Override
    public void updateTimeEntry(TimeEntry tsd) {

        MapSqlParameterSource param = new MapSqlParameterSource();

        param.addValue("tSDayId", tsd.getEntryId());
        param.addValue("timesheetId", tsd.getTimeRecordId());
        param.addValue("empId", tsd.getEmpId());
        param.addValue("dayDate", tsd.getDate());
        param.addValue("workHR", tsd.getWorkHours());
        param.addValue("travelHR", tsd.getTravelHours());
        param.addValue("holidayHR", tsd.getHolidayHours());
        param.addValue("sickEmpHR", tsd.getSickEmpHours());
        param.addValue("sickFamilyHR", tsd.getSickFamHours());
        param.addValue("miscHR", tsd.getMiscHours());
        param.addValue("miscTypeId", tsd.getMiscType().getCode());
        param.addValue("tOriginalUserId", tsd.getTxOriginalUserId());
        param.addValue("tUpdateUserId", tsd.getTxUpdateUserId());
        param.addValue("tOriginalDate", tsd.getTxOriginalDate());
        param.addValue("tUpdateDate", tsd.getTxUpdateDate());
        param.addValue("status", getStatusCode(tsd.isActive()));
        param.addValue("empComment", tsd.getEmpComment());
        param.addValue("payType", tsd.getPayType().name());
        param.addValue("vacationHR", tsd.getVacationHours());
        param.addValue("personalHR", tsd.getPersonalHours());

        localNamedJdbc.update(UPDATE_ENTRY_SQL, param);
    }

    public List<PeriodAccUsage> getLocalPeriodAccrualUsage(int empId, List<PayPeriod> payPeriodList) throws TimeEntryNotFoundEx {

        AccrualUsage au = null;
        List<PeriodAccUsage> pauList = null;

        try{

            for(PayPeriod pp : payPeriodList)
            {
                MapSqlParameterSource param = new MapSqlParameterSource();

                param.addValue("empId",empId);
                param.addValue("startDate", pp.getStartDate());
                param.addValue("endDate", pp.getEndDate());

                au = localNamedJdbc.queryForObject(GET_SUM_OF_HOURS, param, new LocalAccrualUsageRowMapper());

                PeriodAccUsage pau = new PeriodAccUsage();
                pau.setPayPeriod(pp);
                pau.setYear(pp.getEndDate().getYear());
                pau.setEmpId(au.getEmpId());
                pau.setWorkHours(au.getWorkHours());
                pau.setTravelHoursUsed(au.getTravelHoursUsed());
                pau.setHolHoursUsed(au.getHolHoursUsed());
                pau.setVacHoursUsed(au.getVacHoursUsed());
                pau.setEmpHoursUsed(au.getEmpHoursUsed());
                pau.setFamHoursUsed(au.getFamHoursUsed());
                pau.setMiscHoursUsed(au.getMiscHoursUsed());
                pau.setPerHoursUsed(au.getPerHoursUsed());

                pauList.add(pau);
            }

        }catch (DataRetrievalFailureException ex){
            logger.warn("Retrieve Total hours of {} error: {}", empId, ex.getMessage());
            throw new TimeEntryNotFoundEx("No matching Time Entries for Emp id: " + empId);
        }
        return  pauList;


    }
}

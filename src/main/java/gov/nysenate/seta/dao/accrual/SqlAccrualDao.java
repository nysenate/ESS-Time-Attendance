package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.dao.attendance.TimeEntryDao;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.payroll.HolidayDao;
import gov.nysenate.seta.dao.personnel.EmployeeTransactionDao;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.AccrualHistory;
import gov.nysenate.seta.model.accrual.AccrualInfo;
import gov.nysenate.seta.model.accrual.AnnualAccrualRecord;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.personnel.TransactionHistory;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

import static gov.nysenate.seta.model.personnel.TransactionType.*;

@Repository
public class SqlAccrualDao extends SqlBaseDao implements AccrualDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualDao.class);

    @Autowired
    protected EmployeeTransactionDao empTransactionDao;

    @Resource(name = "localTimeEntry")
    protected TimeEntryDao timeEntryDao;

    @Autowired
    protected HolidayDao holidayDao;

    protected static String LATEST_USAGE_SUMS =
        "SELECT \n" +
        "    MAX(DTBEGIN) AS LATEST_DTBEGIN, MAX(DTEND) AS LATEST_DTEND, " +
        "    SUM(NUWORKHRS) AS WORK_HRS, SUM(NUTRVHRS) AS TRV_HRS_USED, SUM(NUHOLHRS) AS HOL_HRS_USED, \n" +
        "    SUM(NUVACHRS) AS VAC_HRS_USED, SUM(NUPERHRS) AS PER_HRS_USED, SUM(NUEMPHRS) AS EMP_HRS_USED,\n" +
        "    SUM(NUFAMHRS) AS FAM_HRS_USED, SUM(NUMISCHRS) AS MISC_HRS_USED, SUM(NUTOTALHRS) AS TOTAL_HRS\n" +
        "FROM PD23ATTEND \n" +
        "WHERE NUXREFEM = :empId AND CDSTATUS = 'A'\n" +
        "AND DTBEGIN >= :startDate";

    protected static String GET_ANNUAL_ACCRUAL_RECORDS_SQL =
        "SELECT \n" +
        "    DTPERIODYEAR AS YEAR, DTCLOSE AS CLOSE_DATE, DTPERLSTPOST DTEND, NUWORKHRSTOT AS WORK_HRS_TOTAL, NUTRVHRSTOT AS TRV_HRS_TOTAL, \n" +
        "    NUVACHRSTOT AS VAC_HRS_USED, NUVACHRSYTD AS VAC_HRS_ACCRUED, NUVACHRSBSD AS VAC_HRS_BANKED,\n" +
        "    NUPERHRSTOT AS PER_HRS_USED, NUPERHRSYTD AS PER_HRS_ACCRUED,\n" +
        "    NUEMPHRSTOT AS EMP_HRS_USED, NUFAMHRSTOT AS FAM_HRS_USED, NUEMPHRSYTD AS EMP_HRS_ACCRUED, NUEMPHRSBSD AS EMP_HRS_BANKED, \n" +
        "    NUHOLHRSTOT AS HOL_HRS_USED, NUMISCHRSTOT AS MISC_HRS_USED, \n" +
        "    NUPAYCTRYTD AS PAY_PERIODS_YTD, NUPAYCTRBSD AS PAY_PERIODS_BANKED\n" +
        "FROM PM23ATTEND WHERE NUXREFEM = :empId AND CDSTATUS = 'A'";


    /** {@inheritDoc} */
    @Override
    public AccrualInfo getAccuralInfo(int empId, PayPeriod payPeriod) throws AccrualException {
        if (payPeriod == null) {
            throw new IllegalArgumentException("Supplied payPeriod cannot be null.");
        }
        else if (!payPeriod.getType().equals(PayPeriodType.AF)) {
            throw new IllegalArgumentException("Supplied payPeriod must be of type AF (Attendance Fiscal).");
        }

        AccrualInfo accrualInfo = new AccrualInfo(empId, payPeriod);

        TransactionHistory transHistory;
        transHistory = empTransactionDao.getTransHistory(empId, new HashSet<>(Arrays.asList(APP, RTP, TYP)));

        DateTime dt = new DateTime(payPeriod.getEndDate());
        int year = dt.get(DateTimeFieldType.year());
        DateTime janDate = new DateTime(year, 1, 1, 0, 0, 0);

        Map<String, Object> usageSums;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("startDate", janDate.toDate());
        usageSums = remoteNamedJdbc.queryForObject(LATEST_USAGE_SUMS, params, new ColumnMapRowMapper());

        logger.info(OutputUtils.toJson(usageSums));

        Map<Integer, AnnualAccrualRecord> annualAccMap = getYearlyAccrualRecords(empId);

        logger.info(OutputUtils.toJson(annualAccMap));

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AccrualHistory getAccrualHistory(int empId, List<PayPeriod> payPeriods) throws AccrualException {
        return null;
    }

    /**
     * Retrieves the annual accrual summaries (from PM23ATTEND). This will have the totals for how many
     * hours were accrued and used for the given year. The records may not reflect data from the most recent
     * pay periods so refer to the 'endDate' indicated in the record.
     *
     * @param empId int - Employee id
     * @return Map&lt;Integer, AnnualAccrualRecord&gt; - { Year -> Annual Accrual Record }
     */
    protected Map<Integer, AnnualAccrualRecord> getYearlyAccrualRecords(int empId) {
        Map<Integer, AnnualAccrualRecord> annualAccRecMap = new HashMap<>();

        MapSqlParameterSource params = new MapSqlParameterSource("empId", empId);
        List<AnnualAccrualRecord> annualAccRecs;
        annualAccRecs = remoteNamedJdbc.query(GET_ANNUAL_ACCRUAL_RECORDS_SQL, params, new AnnualAccRecRowMapper());

        for (AnnualAccrualRecord annualAccRec : annualAccRecs)  {
            annualAccRecMap.put(annualAccRec.getYear(), annualAccRec);
        }

        return annualAccRecMap;
    }

    protected void getAccrualRates(int empId, final Map<Integer, AnnualAccrualRecord> annualAccMap, Date endDate) {
        TransactionHistory expectedHrsHistory;
        expectedHrsHistory = empTransactionDao.getTransHistory(empId, new HashSet<>(Arrays.asList(APP, RTP, MIN)));

        /** If an RA/SA employee is reappointed within a year then they will retain their previous accrual rate
         *  instead of starting over. */

    }

    //  ROUND((CEIL(L_NUMINTOTHRSCUR_N*4)/4)/1820, 4);
}

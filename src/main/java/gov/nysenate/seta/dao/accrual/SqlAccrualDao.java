package gov.nysenate.seta.dao.accrual;

import com.google.common.collect.Sets;
import gov.nysenate.seta.dao.accrual.mapper.AnnualAccSummaryRowMapper;
import gov.nysenate.seta.dao.accrual.mapper.PeriodAccSummaryRowMapper;
import gov.nysenate.seta.dao.accrual.mapper.PeriodAccUsageRowMapper;
import gov.nysenate.seta.dao.attendance.TimeEntryDao;
import gov.nysenate.seta.dao.base.SortOrder;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.dao.personnel.HolidayDao;
import gov.nysenate.seta.dao.transaction.EmpTransactionDao;
import gov.nysenate.seta.model.accrual.*;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.util.DateUtils;
import gov.nysenate.seta.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.seta.dao.accrual.SqlAccrualHelper.*;
import static gov.nysenate.seta.model.transaction.TransactionCode.*;

/**
 * TODO: Document the following things:
 *
 * - Explanation of PM23ATTEND, PD23ATTEND, PD23ACCUSAGE
 * - Why we have to do all this stuff just to get the accruals
 * - Our notions of period/annual usage/summary objects and how they
 *   relate to the tables.
 */
@Deprecated
public class SqlAccrualDao extends SqlBaseDao //implements AccrualDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualDao.class);

    @Autowired
    protected EmpTransactionDao empTransactionDao;

    @Resource(name = "localTimeEntry")
    protected TimeEntryDao localTimeEntryDao;

    @Autowired
    protected PayPeriodDao payPeriodDao;

    @Autowired
    protected HolidayDao holidayDao;

    protected static final Set<TransactionCode> PAY_CODES = Sets.newHashSet(TYP, RTP, APP);
    protected static final Set<TransactionCode> MIN_CODES = Sets.newHashSet(MIN, RTP, APP);

    protected static final Set<TransactionCode> APP_RTP_CODES = Sets.newHashSet(RTP, APP);
    protected static final Set<TransactionCode> EMP_CODES = Sets.newHashSet(EMP);
    protected static final Set<TransactionCode> APP_RTP_EMP_CODES = Sets.newHashSet(EMP, RTP, APP);

    /** --- Public Interface --- */

    /** {@inheritDoc} */
    public PeriodAccSummary getAccuralSummary(int empId, PayPeriod payPeriod, boolean earliestRecLikeAppoint) throws AccrualException {


        LocalDate startDate = payPeriod.getStartDate();
        LocalDate endDate = payPeriod.getEndDate();
        int year = endDate.getYear();

        Map<Integer, AnnualAccSummary> annualSummaries = getAnnualAccrualSummaries(empId, year);
        LinkedList<PeriodAccSummary> periodSummaries = getPeriodAccrualSummaries(empId, year, startDate);

        return getAccuralSummary(empId, payPeriod, annualSummaries, periodSummaries, earliestRecLikeAppoint);
    }

    /** {@inheritDoc} */
    public List<PeriodAccSummary> getAccrualSummaries(int empId, List<PayPeriod> payPeriods) throws AccrualException {
        return null;
    }

    /** --- Processing Methods --- */

    private PeriodAccSummary getAccuralSummary(int empId, PayPeriod payPeriod,
                                                   Map<Integer, AnnualAccSummary> annualSummaries,
                                                   LinkedList<PeriodAccSummary> periodSummaries,
                                                   boolean earliestRecLikeAppoint) throws AccrualException {

        LocalDate payPeriodEndDate = payPeriod.getEndDate();
        LocalDate prevPeriodEndDate = getPrevPayPeriodEndDate(payPeriod);

        /** We first check to see if there is a period summary for the previous pay period. If it's available
         *  then we can simply return the summary and we're done. */
        PeriodAccSummary latestPeriodSum = periodSummaries.poll();
        if (periodSummaryIsCurrent(latestPeriodSum, payPeriod)) {
//            latestPeriodSum.setPayPeriod(payPeriod);
            return latestPeriodSum;
        }

        logger.debug("Period Accrual Summary not found for empId: {} and end date: {}", empId, prevPeriodEndDate);

        /** Since we didn't have a matching period summary record, the accruals must be rebuilt up to the given
         *  pay period. We look for an annual summary record because those have counts of how many pay periods
         *  were worked and are initially created by personnel. */
        AnnualAccSummary activeAnnualSummary = getActiveAnnualSummaryFromList(annualSummaries, payPeriodEndDate);

        /** If there are no annual summary records to work with we don't have enough information to reliably
         *  construct a period accrual summary. We can throw an error instead. */
        if (activeAnnualSummary == null) {
            logger.warn("Annual Accrual Summary could not be found for any given year for empId: {}", empId);
            throw new AccrualException(empId, AccrualExceptionType.NO_ACTIVE_ANNUAL_RECORD_FOUND);
        }

        LocalDate activeAnnualEndDate = activeAnnualSummary.getEndDate();

        /** The annual summary will likely not be current if the period summary wasn't, but check anyways. */
        if (annualSummaryIsCurrent(activeAnnualSummary, payPeriod)) {
            logger.debug("Annual Accrual Summary is current, yet the Period Summary is not.");
            // calculate expected hours and return
            return null;
        }

        /** A missing end date from the annual summary indicates that no pay periods have been processed for
         *  the summary. This usually occurs for (re)appointed employees, temps, and those on unpaid leave. */
        boolean hasEndDate = true;
        if (activeAnnualSummary.getEndDate() == null) {
            activeAnnualEndDate = LocalDate.of(activeAnnualSummary.getYear() - 1, 12, 31);
            hasEndDate = false;
        }

        /** Get the transactions that occurred before/on the end date of the annual summary. */
        TransactionHistory historyBeforeSummary = getAccrualTransactions(empId, DateUtils.longAgo(), activeAnnualEndDate);

        /** Establish the initial state based on the transaction records. */
        AccrualState accrualState = getInitialAccrualState(activeAnnualSummary, activeAnnualEndDate,
                                                           historyBeforeSummary, hasEndDate);

        logger.debug("Initial accrual state: {}", OutputUtils.toJson(accrualState));

        /** The gap is the date range for which we want to compute accruals for as well as determine how many
         *  hours were used (period usage records from PD23ATTEND or the local time sheet database. */
        LocalDate gapStartDate = activeAnnualEndDate.plusDays(1);
        LocalDate gapEndDate = prevPeriodEndDate;
        AccrualGap accrualGap = new AccrualGap(empId, gapStartDate, gapEndDate);

        logger.debug("Determining accruals between {} and {}", gapStartDate, gapEndDate);

//        TransactionHistory historyDuringGap = getAccrualTransactions(empId, accrualGap.getStartDate(), accrualGap.getEndDate());
//        accrualGap.setGapPeriods(payPeriodDao.getPayPeriods(PayPeriodType.AF, gapStartDate, gapEndDate, true));
//        accrualGap.setRecordsDuringGap(historyDuringGap.getAllTransRecords(SortOrder.DESC));
        accrualGap.setPeriodUsageRecs(getPeriodAccrualUsageRecords(empId, gapStartDate, gapEndDate));

        /** TODO: Add local timesheet data */

        for (PayPeriod gapPeriod : accrualGap.getGapPeriods()) {
            accrualState.setEndDate(gapPeriod.getEndDate());

            /** Look through the transactions that occur before or on the end date of the pay period */
            LinkedList<TransactionRecord> recordsInPeriod = accrualGap.getTransRecsDuringPeriod(gapPeriod);

            /** Also check if any usage records (PD23ATTEND) exist for the current gap period */
//            PeriodAccUsage periodUsage = accrualGap.getUsageRecDuringPeriod(gapPeriod);

            /** If the employee is set as terminated we want to skip accruals until they are reappointed */
            if (!accrualState.isEmployeeActive()) {
                if (!isEmployeeAppointed(recordsInPeriod)) {
                    continue;
                }
                else {
                    logger.debug("Employee has been (re)appointed this period.");
                }
            }

            /** If the year has rolled over, reset the accrual state. */
            if (accrualState.getEndDate().getYear() != gapPeriod.getEndDate().getYear()) {
                accrualState.applyYearRollover();
            }

            logger.debug("Computing accruals for pay period: {}", gapPeriod);

            BigDecimal minTotalHours = getMinTotalHours(recordsInPeriod);
            BigDecimal minEndHours = getMinEndHours(recordsInPeriod);
            PayType payType = getPayType(recordsInPeriod);
            accrualState.setEmployeeActive(!isEmployeeTerminated(recordsInPeriod));

            if (minTotalHours != null) {
                accrualState.setMinTotalHours(minTotalHours);
            }
            if (minEndHours != null) {
                accrualState.setMinHoursToEnd(minEndHours);
            }
            if (payType != null) {
                accrualState.setPayType(payType);
            }

            boolean isTemp = accrualState.getPayType().equals(PayType.TE);
            boolean isEndOfYearSplit = gapPeriod.isEndOfYearSplit();

            if (!isTemp && !isEndOfYearSplit) {
                accrualState.setPeriodCounter(accrualState.getPeriodCounter() + 1);
                accrualState.setVacRate(AccrualRate.VACATION.getRate(accrualState.getPeriodCounter(), accrualState.getProratePercentage()));
                accrualState.setSickRate(AccrualRate.SICK.getRate(accrualState.getPeriodCounter(), accrualState.getProratePercentage()));
                accrualState.incrementAccrualsEarned();
            }

//            if (periodUsage != null) {
//                accrualState.applyUsage(periodUsage);
//            }

            logger.debug("Current accrual state: {}", OutputUtils.toJson(accrualState));
        }

        return null;
    }

    /**
     * Initializes an AccrualState object with information from the annual summary.
     */
    protected AccrualState getInitialAccrualState(AnnualAccSummary annSummary, LocalDate endDate,
                                                  TransactionHistory transHistory, boolean hasEndDate) throws AccrualException {
        LinkedList<TransactionRecord> empRecords = getEmpRecordsFromHistory(transHistory, SortOrder.DESC);
        LinkedList<TransactionRecord> minRecords = getMinRecordsFromHistory(transHistory, SortOrder.DESC);
        LinkedList<TransactionRecord> payTypeRecords = getPayTypeRecordsFromHistory(transHistory, SortOrder.DESC);

        AccrualState accState = new AccrualState(annSummary);
        accState.setEndDate(endDate);
        accState.setPeriodCounter(annSummary.getPayPeriodsBanked());
        accState.setEmployeeActive(hasEndDate && !isEmployeeTerminated(empRecords));
        accState.setPayType(getPayType(payTypeRecords));
        accState.setMinTotalHours(getMinTotalHours(minRecords));
        accState.setMinHoursToEnd(getMinEndHours(minRecords));

        if (hasEndDate) {
            if (accState.getMinTotalHours() == null) {
                throw new AccrualException(annSummary.getEmpId(), AccrualExceptionType.NO_MIN_TRANSACTIONS_FOUND);
            }
            if (accState.getPayType() == null) {
                throw new AccrualException(annSummary.getEmpId(), AccrualExceptionType.NO_TYP_TRANSACTIONS_FOUND);
            }
        }

        return accState;
    }

    /**
     * Get a TransactionHistory for an employee that contains any transactions relevant to accrual
     * processing logic.
     */
    private TransactionHistory getAccrualTransactions(int empId, LocalDate startDate, LocalDate endDate) {
        Set<TransactionCode> codes = new HashSet<>(Arrays.asList(APP, RTP, TYP, MIN, EMP));
        return empTransactionDao.getTransHistory(empId, codes, toDate(startDate), toDate(endDate), true);
    }

    /** --- Data Retrieval Methods -- */

    /**
     * Retrieves the annual accrual summaries (from PM23ATTEND). This will have the totals for how many
     * hours were accrued and used for the given year. The records may not reflect data from the most recent
     * pay periods so refer to the 'endDate' indicated in the record.
     *
     * @param empId int - Employee id
     * @param endYear int - Year to retrieve summaries until
     * @return Map&lt;Integer, AnnualAccSummary&gt; - { Year -> Annual Accrual Record }
     */
    protected Map<Integer, AnnualAccSummary> getAnnualAccrualSummaries(int empId, int endYear) {
        Map<Integer, AnnualAccSummary> annualAccRecMap = new HashMap<>();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("endYear", endYear);
        List<AnnualAccSummary> annualAccRecs;
        annualAccRecs = remoteNamedJdbc.query(GET_ANNUAL_ACCRUAL_SUMMARIES_SQL, params,
                                              new AnnualAccSummaryRowMapper());
        for (AnnualAccSummary annualAccRec : annualAccRecs)  {
            annualAccRecMap.put(annualAccRec.getYear(), annualAccRec);
        }
        return annualAccRecMap;
    }

    /**
     * Retrieves accrual summaries per pay period (from PD23ACCUSAGE) up until an end date. The results will include
     * entries from this year and last year as well in case the year just started. The returned list will be ordered
     * from most recent pay period first.
     *
     * @param empId int - Employee id
     * @param year int - Year
     * @param beforeDate Date - End Date
     * @return List<PeriodAccSummary>
     */
    protected LinkedList<PeriodAccSummary> getPeriodAccrualSummaries(int empId, int year, LocalDate beforeDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("prevYear", year - 1);
        params.addValue("beforeDate", beforeDate);
        return new LinkedList<>(remoteNamedJdbc.query(GET_PERIOD_ACCRUAL_SUMMARY_SQL, params,
                                                      new PeriodAccSummaryRowMapper("","")));
    }

    /**
     * (from PD23ATTEND)
     *
     * @param empId int - Employee id
     * @param startDate Date - Start date (inclusive)
     * @param endDate Date - End Date (inclusive)
     * @return LinkedList<PeriodAccUsage>
     */
    protected LinkedList<PeriodAccUsage> getPeriodAccrualUsageRecords(int empId, LocalDate startDate, LocalDate endDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("startDate", startDate);
        params.addValue("endDate", endDate);
        return new LinkedList<>(remoteNamedJdbc.query(GET_PERIOD_ACCRUAL_USAGE_SQL, params,
                                                      new PeriodAccUsageRowMapper("","")));
    }


    /** --- SQL Queries --- */

    protected static final String GET_ANNUAL_ACCRUAL_SUMMARIES_SQL =
            "SELECT \n" +
                    "    NUXREFEM, DTPERIODYEAR AS YEAR, DTCLOSE AS CLOSE_DATE, DTPERLSTPOST AS DTEND, " +
                    "    DTCONTSERV AS CONT_SERVICE_DATE, NUWORKHRSTOT AS WORK_HRS, NUTRVHRSTOT AS TRV_HRS_USED, \n" +
                    "    NUVACHRSTOT AS VAC_HRS_USED, NUVACHRSYTD AS VAC_HRS_ACCRUED, NUVACHRSBSD AS VAC_HRS_BANKED,\n" +
                    "    NUPERHRSTOT AS PER_HRS_USED, NUPERHRSYTD AS PER_HRS_ACCRUED,\n" +
                    "    NUEMPHRSTOT AS EMP_HRS_USED, NUFAMHRSTOT AS FAM_HRS_USED, NUEMPHRSYTD AS EMP_HRS_ACCRUED, \n" +
                    "    NUEMPHRSBSD AS EMP_HRS_BANKED, NUHOLHRSTOT AS HOL_HRS_USED, NUMISCHRSTOT AS MISC_HRS_USED, \n" +
                    "    NUPAYCTRYTD AS PAY_PERIODS_YTD, NUPAYCTRBSD AS PAY_PERIODS_BANKED\n" +
                    "FROM PM23ATTEND WHERE NUXREFEM = :empId AND DTPERIODYEAR <= :endYear";

    protected static final String GET_PERIOD_ACCRUAL_SUMMARY_SQL =
            "SELECT \n" +
                    "    NUXREFEM, acc.DTPERIODYEAR AS YEAR, NUTOTHRSLAST AS PREV_TOTAL_HRS," +
                    "    NUHRSEXPECT AS EXPECTED_TOTAL_HRS, NUVACHRSUSE AS VAC_HRS_USED, NUPERHRSUSE AS PER_HRS_USED, " +
                    "    NUEMPHRSUSE AS EMP_HRS_USED, NUFAMHRSUSE AS FAM_HRS_USED, NUHOLHRSUSE AS HOL_HRS_USED, " +
                    "    NUMISCHRSUSE AS MISC_HRS_USED, NUTRVHRSUSE AS TRV_HRS_USED, NUWRKHRSTOT AS WORK_HRS, \n" +
                    "    NUVACHRSACC AS VAC_HRS_ACCRUED, NUPERHRSACC AS PER_HRS_ACCRUED, NUEMPHRSACC AS EMP_HRS_ACCRUED, \n" +
                    "    NUVACHRSBSD AS VAC_HRS_BANKED, NUEMPHRSBSD AS EMP_HRS_BANKED, NUBIWHRSEXP AS EXPECTED_BIWEEK_HRS, " +
                    "    NUBIWSICRATE AS SICK_RATE, NUBIWVACRATE AS VAC_RATE,\n" +
                    "    per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD\n" +
                    "FROM PD23ACCUSAGE acc\n" +
                    "JOIN (SELECT * FROM SL16PERIOD WHERE CDPERIOD = 'AF') per ON acc.DTEND = per.DTEND\n" +
                    "WHERE acc.NUXREFEM = :empId AND acc.DTPERIODYEAR >= :prevYear AND acc.DTEND < :beforeDate\n" +
                    "ORDER BY acc.DTEND DESC";

    protected static final String GET_PERIOD_ACCRUAL_USAGE_SQL =
            "SELECT \n" +
                    "    NUXREFEM, NUWORKHRS AS WORK_HRS, NUTRVHRS AS TRV_HRS_USED, NUHOLHRS AS HOL_HRS_USED, NUPERHRS AS PER_HRS_USED,\n" +
                    "    NUEMPHRS AS EMP_HRS_USED, NUFAMHRS AS FAM_HRS_USED, NUVACHRS AS VAC_HRS_USED,\n" +
                    "    NUMISCHRS AS MISC_HRS_USED, att.DTPERIODYEAR AS YEAR,\n" +
                    "    per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD\n" +
                    "FROM PD23ATTEND att\n" +
                    "JOIN (SELECT * FROM SL16PERIOD WHERE CDPERIOD = 'AF') per ON att.DTEND = per.DTEND\n" +
                    "WHERE att.NUXREFEM = :empId AND per.DTBEGIN >= :startDate AND per.DTEND <= :endDate\n" +
                    "ORDER BY att.DTBEGIN ASC";
}
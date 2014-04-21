package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.dao.accrual.mapper.AnnualAccrualSummaryRowMapper;
import gov.nysenate.seta.dao.accrual.mapper.AnnualAccrualUsageRowMapper;
import gov.nysenate.seta.dao.accrual.mapper.PeriodAccrualSummaryRowMapper;
import gov.nysenate.seta.dao.accrual.mapper.PeriodAccrualUsageRowMapper;
import gov.nysenate.seta.dao.attendance.TimeEntryDao;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.payroll.HolidayDao;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.dao.personnel.EmployeeTransactionDao;
import gov.nysenate.seta.model.accrual.*;
import gov.nysenate.seta.model.exception.PayPeriodException;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.personnel.TransactionHistory;
import gov.nysenate.seta.model.personnel.TransactionRecord;
import gov.nysenate.seta.model.personnel.TransactionType;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static gov.nysenate.seta.model.personnel.TransactionType.*;

/**
 * TODO: Document the following things:
 *
 * - Explanation of PM23ATTEND, PD23ATTEND, PD23ACCUSAGE
 * - Why we have to do all this stuff just to get the accruals
 * - Our notions of period/annual usage/summary objects and how they
 *   relate to the tables.
 */
@Repository
public class SqlAccrualDao extends SqlBaseDao implements AccrualDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualDao.class);

    @Autowired
    protected EmployeeTransactionDao empTransactionDao;

    @Resource(name = "localTimeEntry")
    protected TimeEntryDao localTimeEntryDao;

    @Autowired
    protected PayPeriodDao payPeriodDao;

    @Autowired
    protected HolidayDao holidayDao;

    protected static final Set<TransactionType> PAY_TYPES = new HashSet<>(Arrays.asList(TYP, RTP, APP));
    protected static final Set<TransactionType> MIN_TYPES = new HashSet<>(Arrays.asList(MIN, RTP, APP));

    protected static final Set<TransactionType> APP_RTP_TYPES = new HashSet<>(Arrays.asList(RTP, APP));
    protected static final Set<TransactionType> EMP_TYPE = new HashSet<>(Arrays.asList(EMP));
    protected static final Set<TransactionType> APP_RTP_EMP_TYPES = new HashSet<>(Arrays.asList(EMP, RTP, APP));

    /** --- SQL Queries --- */

    protected static final String LATEST_USAGE_SUMS =
        "SELECT \n" +
        "    MAX(DTBEGIN) AS LATEST_DTBEGIN, MAX(DTEND) AS LATEST_DTEND, " +
        "    SUM(NUWORKHRS) AS WORK_HRS, SUM(NUTRVHRS) AS TRV_HRS_USED, SUM(NUHOLHRS) AS HOL_HRS_USED, \n" +
        "    SUM(NUVACHRS) AS VAC_HRS_USED, SUM(NUPERHRS) AS PER_HRS_USED, SUM(NUEMPHRS) AS EMP_HRS_USED,\n" +
        "    SUM(NUFAMHRS) AS FAM_HRS_USED, SUM(NUMISCHRS) AS MISC_HRS_USED, SUM(NUTOTALHRS) AS TOTAL_HRS\n" +
        "FROM PD23ATTEND \n" +
        "WHERE NUXREFEM = :empId AND CDSTATUS = 'A'\n" +
        "AND DTBEGIN >= :startDate";

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

    /** --- Public Interface --- */

    /** {@inheritDoc} */
    @Override
    public PeriodAccrualSummary getAccuralSummary(int empId, PayPeriod payPeriod) throws AccrualException {
        if (payPeriod == null) {
            throw new IllegalArgumentException("Supplied payPeriod cannot be null.");
        }
        else if (!payPeriod.getType().equals(PayPeriodType.AF)) {
            throw new IllegalArgumentException("Supplied payPeriod must be of type AF (Attendance Fiscal).");
        }

        Date startDate = payPeriod.getStartDate();
        Date endDate = payPeriod.getEndDate();
        int year = new LocalDate(endDate).getYear();

        Map<Integer, AnnualAccrualSummary> annualSummaries = getAnnualAccrualSummaries(empId, year);
        LinkedList<PeriodAccrualSummary> periodSummaries = getPeriodAccrualSummaries(empId, year, startDate);

        return getAccuralSummary(empId, payPeriod, annualSummaries, periodSummaries);
    }

    /** {@inheritDoc} */
    @Override
    public List<PeriodAccrualSummary> getAccrualSummaries(int empId, List<PayPeriod> payPeriods) throws AccrualException {
        return null;
    }

    /** --- Processing Methods --- */

    private PeriodAccrualSummary getAccuralSummary(int empId, PayPeriod payPeriod,
                                                   Map<Integer, AnnualAccrualSummary> annualSummaries,
                                                   LinkedList<PeriodAccrualSummary> periodSummaries) throws AccrualException {

        Date payPeriodEndDate = payPeriod.getEndDate();
        Date prevPeriodEndDate = getPrevPayPeriodEndDate(payPeriod);
        int year = new LocalDate(payPeriodEndDate).getYear();

        /** We first check to see if there is a period summary for the previous pay period. If it's available
         *  then we can simply return the summary and we're done. */
        PeriodAccrualSummary latestPeriodSum = periodSummaries.poll();
        if (periodSummaryIsCurrent(latestPeriodSum, payPeriod)) {
            latestPeriodSum.setPayPeriod(payPeriod);
            //return latestPeriodSum;
        }

        logger.debug("Period Accrual Summary not found for empId: {} and end date: {}", empId, prevPeriodEndDate);

        /** Since we didn't have a matching period summary record, the accruals must be rebuilt up to the given
         *  pay period. We look for an annual summary record because those have counts of how many pay periods
         *  were worked and are initially created by personnel. */
        AnnualAccrualSummary activeAnnualSummary = getActiveAnnualSummaryFromList(annualSummaries, payPeriodEndDate);

        /** If there are no annual summary records to work with we don't have enough information to reliably
         *  construct a period accrual summary. We can throw an error instead. */
        if (activeAnnualSummary == null) {
            logger.warn("Annual Accrual Summary could not be found for any given year for empId: {}", empId);
            throw new AccrualException(empId, AccrualExceptionType.NO_ACTIVE_ANNUAL_RECORD_FOUND);
        }

        //Date activeAnnualStartDate = new LocalDate(activeAnnualSummary.getYear(), 1, 1).toDate();
        Date activeAnnualEndDate = activeAnnualSummary.getEndDate();

        /** The annual summary will likely not be current if the period summary wasn't, but check anyways. */
        if (annualSummaryIsCurrent(activeAnnualSummary, payPeriod)) {
            logger.debug("Annual Accrual Summary is current, yet the Period Summary is not.");
            // calculate expected hours and return
            return null;
        }

        /** A missing end date from the annual summary indicates that no pay periods have been processed for
         *  the summary. This usually occurs for (re)appointed employee's, temps, and those on unpaid leave. */
        if (activeAnnualSummary.getEndDate() == null) {
            activeAnnualEndDate = getActiveDateForSummaryMissingEndDate(empId, payPeriodEndDate, activeAnnualSummary);
            if (activeAnnualEndDate == null) {
            //    activeAnnualStartDate = new LocalDate(activeAnnualSummary.getYear() - 1, 1, 1).toDate();
                activeAnnualEndDate = new LocalDate(activeAnnualSummary.getYear() - 1, 12, 31).toDate();
            }
            logger.debug("Setting annual summary end date to {}", activeAnnualEndDate);
        }

        /** Get the transactions that occurred before/on the end date of the annual summary. */
        TransactionHistory historyBeforeSummary = getAccrualTransactions(empId, getBeginningOfTime(), activeAnnualEndDate);

        /** Establish the initial state based on the transaction records. */
        AccrualState accrualState = getInitialAccrualState(activeAnnualSummary, activeAnnualEndDate, historyBeforeSummary);

        logger.debug("Initial accrual state: {}", OutputUtils.toJson(accrualState));

        /** The gap is the date range for which we want to compute accruals for as well as determine how many
         *  hours were used (period usage records from PD23ATTEND or the local time sheet database. */
        Date gapStartDate = new LocalDate(activeAnnualEndDate).plusDays(1).toDate();
        Date gapEndDate = prevPeriodEndDate;
        AccrualGap accrualGap = new AccrualGap(empId, gapStartDate, gapEndDate);

        logger.debug("Determining accruals between {} and {}", gapStartDate, gapEndDate);

        TransactionHistory historyDuringGap = getAccrualTransactions(empId, accrualGap.getStartDate(), accrualGap.getEndDate());
        accrualGap.setGapPeriods(payPeriodDao.getPayPeriods(PayPeriodType.AF, gapStartDate, gapEndDate, true));
        accrualGap.setRecordsDuringGap(historyDuringGap.getAllTransRecords(false));
        accrualGap.setPeriodUsageRecs(getPeriodAccrualUsageRecords(empId, gapStartDate, gapEndDate));

        Iterator<PayPeriod> gapPeriodIterator = accrualGap.getGapPeriods().iterator();

        AccrualUsage usageDuringGap = new AccrualUsage();
        while (gapPeriodIterator.hasNext()) {
            PayPeriod gapPeriod = gapPeriodIterator.next();

            /** Look through the transactions that occur before or on the end date of the pay period */
            LinkedList<TransactionRecord> recordsInPeriod = accrualGap.getTransRecsDuringPeriod(gapPeriod);

            /** Also check if any usage records (PD23ATTEND) exist for the current gap period */
            PeriodAccrualUsage periodUsage = accrualGap.getUsageRecDuringPeriod(gapPeriod);

            /** If the employee is set as terminated we want to skip accruals until they are reappointed */
            if (accrualState.isTerminated()) {
                if (!isEmployeeAppointed(recordsInPeriod)) {
                    continue;
                }
            }

            if (isEmployeeAppointed(recordsInPeriod)) {
                logger.debug("Employee has been (re)appointed this period.");
            }

            /** If the year has rolled over, reset the accrual state. */
            if (new LocalDate(accrualState.getEndDate()).getYear() != new LocalDate(gapPeriod.getEndDate()).getYear()) {
                accrualState.applyYearRollover();
            }

            logger.debug("Computing accruals for pay period: {}", gapPeriod);

            BigDecimal minTotalHours = getMinTotalHours(recordsInPeriod);
            BigDecimal minEndHours = getMinEndHours(recordsInPeriod);
            PayType payType = getPayType(recordsInPeriod);
            accrualState.setTerminated(isEmployeeTerminated(recordsInPeriod));

            if (minTotalHours != null) {
                accrualState.setMinTotalHours(minTotalHours);
            }
            if (minEndHours != null) {
                accrualState.setMinHoursToEnd(minEndHours);
            }
            if (payType != null) {
                accrualState.setPayType(payType);
            }

            accrualState.setEndDate(gapPeriod.getEndDate());
            accrualState.setPeriodCounter(accrualState.getPeriodCounter() + 1);

            accrualState.setVacRate(AccrualRate.VACATION.getRate(accrualState.getPeriodCounter(), accrualState.getProratePercentage()));
            accrualState.setSickRate(AccrualRate.SICK.getRate(accrualState.getPeriodCounter(), accrualState.getProratePercentage()));
            accrualState.incrementAccrualsEarned();

            if (periodUsage != null) {
                accrualState.applyUsage(periodUsage);
            }

            logger.debug("Current accrual state: {}", OutputUtils.toJson(accrualState));
        }

        return null;
    }

    protected AccrualState getInitialAccrualState(AnnualAccrualSummary annSummary, Date endDate, TransactionHistory transHistory)
              throws AccrualException {
        LinkedList<TransactionRecord> empRecords = getEmpRecordsFromHistory(transHistory, false);
        LinkedList<TransactionRecord> minRecords = getMinRecordsFromHistory(transHistory, false);
        LinkedList<TransactionRecord> payTypeRecords = getPayTypeRecordsFromHistory(transHistory, false);

        AccrualState accState = new AccrualState(annSummary);
        accState.setEndDate(endDate);
        accState.setPeriodCounter(annSummary.getPayPeriodsBanked());
        accState.setTerminated(isEmployeeTerminated(empRecords));
        accState.setPayType(getPayType(payTypeRecords));
        accState.setMinTotalHours(getMinTotalHours(minRecords));
        accState.setMinHoursToEnd(getMinEndHours(minRecords));

        /** TODO: Throw errors if null */

        return accState;
    }

    /**
     * Gets the latest annual accrual record that has a posted end date that is before our given 'payPeriodEndDate'.
     */
    private AnnualAccrualSummary getActiveAnnualSummaryFromList(Map<Integer, AnnualAccrualSummary> annualSummaries,
                                                                Date payPeriodEndDate) {
        AnnualAccrualSummary annualAccSum = null;
        if (!annualSummaries.isEmpty()) {
            Iterator<Integer> yearIterator = new TreeSet<>(annualSummaries.keySet()).descendingIterator();
            while (yearIterator.hasNext()) {
                annualAccSum = annualSummaries.get(yearIterator.next());
                if (annualAccSum.getEndDate() == null || annualAccSum.getEndDate().before(payPeriodEndDate)) {
                    break;
                }
            }
        }
        return annualAccSum;
    }

    /**
     * Get the end date of the previous pay period. Assumes pay period is contiguous.
     */
    private Date getPrevPayPeriodEndDate(PayPeriod payPeriod) {
        return new LocalDate(payPeriod.getStartDate()).minusDays(1).toDate();
    }

    /**
     * Checks if the period summary record has an end date that matches the period preceding the
     * given 'payPeriod'. This indicates it's current because since accruals are applied after a
     * period elapses and a summary record represents the state of a period after it's elapsed, a
     * pay period's available hours is essentially determined from the previous pay period's totals.
     */
    private boolean periodSummaryIsCurrent(PeriodAccrualSummary periodSummary, PayPeriod payPeriod) {
        Date prevPayPeriodEndDate = getPrevPayPeriodEndDate(payPeriod);
        return (periodSummary != null && periodSummary.getEndDate().compareTo(prevPayPeriodEndDate) == 0);
    }

    /**
     * Similar to 'periodSummaryIsCurrent' but for annual accrual summary.
     */
    private boolean annualSummaryIsCurrent(AnnualAccrualSummary annualSummary, PayPeriod payPeriod) {
        //logger.debug(OutputUtils.toJson(annualSummary));
        Date prevPayPeriodEndDate = getPrevPayPeriodEndDate(payPeriod);
        return (annualSummary != null && annualSummary.getEndDate() != null &&
                annualSummary.getEndDate().compareTo(prevPayPeriodEndDate) == 0);
    }

    /**
     * Get a TransactionHistory for an employee that contains any transactions relevant to accrual
     * processing logic.
     */
    private TransactionHistory getAccrualTransactions(int empId, Date startDate, Date endDate) {
        Set<TransactionType> types = new HashSet<>(Arrays.asList(APP, RTP, TYP, MIN, EMP));
        return empTransactionDao.getTransHistory(empId, types, startDate, endDate);
    }

    /**
     * Attempts to handle the most likely cases when the end date is missing from the annual accrual summary.
     *
     */
    private Date getActiveDateForSummaryMissingEndDate(int empId, Date payPeriodEndDate, AnnualAccrualSummary activeAnnualSummary)
                                                       throws AccrualException {
        Date startOfSummaryYear = new LocalDate(activeAnnualSummary.getYear(), 1, 1).toDate();
        TransactionHistory historyDuringYear = getAccrualTransactions(empId, startOfSummaryYear, payPeriodEndDate);
        TransactionRecord rec = historyDuringYear.getTransRecords(APP_RTP_TYPES, false).getFirst();
        if (rec != null) {
            try {
                PayPeriod appointedPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, rec.getEffectDate());
                return getPrevPayPeriodEndDate(appointedPeriod);
            }
            catch (PayPeriodException ex) {
                logger.warn("Pay period during {} could not be found.", rec.getEffectDate());
            }
        }
        return null;
    }

    /**
     * Returns true if the first item in the list of trans records indicates that the employee was terminated.
     */
    private boolean isEmployeeTerminated(LinkedList<TransactionRecord> empRecords) {
        for (TransactionRecord rec : empRecords) {
            if (APP_RTP_TYPES.contains(rec.getTransType())) {
                return false;
            }
            else if (EMP_TYPE.contains(rec.getTransType())) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmployeeAppointed(LinkedList<TransactionRecord> empRecords) {
        for (TransactionRecord rec : empRecords) {
            if (APP_RTP_TYPES.contains(rec.getTransType())) {
                return true;
            }
            else if (EMP_TYPE.contains(rec.getTransType())) {
                return false;
            }
        }
        return false;
    }

    /**
     * Looks up the first non null min pay type value in the given list of trans recs.
     * Returns null if nothing found.
     */
    private PayType getPayType(LinkedList<TransactionRecord> payTypeRecs) throws AccrualException {
        for (TransactionRecord rec : payTypeRecs) {
            if (PAY_TYPES.contains(rec.getTransType()) && rec.hasNonNullValue("CDPAYTYPE")) {
                return PayType.valueOf(rec.getValue("CDPAYTYPE"));
            }
        }
        return null;
    }

    /**
     * Looks up the first non null min hours value in the given list of trans recs.
     * Returns null if nothing found.
     */
    private BigDecimal getMinTotalHours(LinkedList<TransactionRecord> minHourRecs) {
        for (TransactionRecord rec : minHourRecs) {
            if (MIN_TYPES.contains(rec.getTransType()) && rec.getValue("NUMINTOTHRS") != null) {
                return new BigDecimal(rec.getValue("NUMINTOTHRS"));
            }
        }
        return null;
    }

    /**
     * Looks up the first non null 'min hours until end of year' value in the given list of trans recs.
     * Retursn null if nothing found.
     */
    private BigDecimal getMinEndHours(LinkedList<TransactionRecord> minHourRecs) throws AccrualException {
        for (TransactionRecord rec : minHourRecs) {
            if (MIN_TYPES.contains(rec.getTransType()) && rec.getValue("NUMINTOTEND") != null) {
                return new BigDecimal(rec.getValue("NUMINTOTEND"));
            }
        }
        return null;
    }

    /** Returns a list of just min type transactions from the given history */
    private LinkedList<TransactionRecord> getMinRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionType> types = new LinkedHashSet<>(Arrays.asList(MIN, RTP, APP));
        return transHistory.getTransRecords(types, orderByAsc);
    }

    /** Returns a list of just employee status transactions from the given history */
    private LinkedList<TransactionRecord> getEmpRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionType> types = new LinkedHashSet<>(Arrays.asList(RTP, EMP, APP));
        return transHistory.getTransRecords(types, orderByAsc);
    }

    /** Returns a list of just pay type transactions from the given history */
    private LinkedList<TransactionRecord> getPayTypeRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionType> types = new LinkedHashSet<>(Arrays.asList(TYP, RTP, APP));
        return transHistory.getTransRecords(types, orderByAsc);
    }

    /** --- Data Retrieval Methods -- */

    /**
     * Retrieves the annual accrual summaries (from PM23ATTEND). This will have the totals for how many
     * hours were accrued and used for the given year. The records may not reflect data from the most recent
     * pay periods so refer to the 'endDate' indicated in the record.
     *
     * @param empId int - Employee id
     * @param endYear int - Year to retrieve summaries until
     * @return Map&lt;Integer, AnnualAccrualSummary&gt; - { Year -> Annual Accrual Record }
     */
    protected Map<Integer, AnnualAccrualSummary> getAnnualAccrualSummaries(int empId, int endYear) {
        Map<Integer, AnnualAccrualSummary> annualAccRecMap = new HashMap<>();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("endYear", endYear);
        List<AnnualAccrualSummary> annualAccRecs;
        annualAccRecs = remoteNamedJdbc.query(GET_ANNUAL_ACCRUAL_SUMMARIES_SQL, params,
                                              new AnnualAccrualSummaryRowMapper());
        for (AnnualAccrualSummary annualAccRec : annualAccRecs)  {
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
     * @return List<PeriodAccrualSummary>
     */
    protected LinkedList<PeriodAccrualSummary> getPeriodAccrualSummaries(int empId, int year, Date beforeDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("prevYear", year - 1);
        params.addValue("beforeDate", beforeDate);
        return new LinkedList<>(remoteNamedJdbc.query(GET_PERIOD_ACCRUAL_SUMMARY_SQL, params,
                                                      new PeriodAccrualSummaryRowMapper("","")));
    }

    /**
     * (from PD23ATTEND)
     *
     * @param empId int - Employee id
     * @param startDate Date - Start date (inclusive)
     * @param endDate Date - End Date (inclusive)
     * @return LinkedList<PeriodAccrualUsage>
     */
    protected LinkedList<PeriodAccrualUsage> getPeriodAccrualUsageRecords(int empId, Date startDate, Date endDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("startDate", startDate);
        params.addValue("endDate", endDate);
        return new LinkedList<>(remoteNamedJdbc.query(GET_PERIOD_ACCRUAL_USAGE_SQL, params,
                                                      new PeriodAccrualUsageRowMapper("","")));
    }

    /**
     * Retrieves the latest accrual usage stats for the given year (from PD23ATTEND). This is just the sum
     * of the hours used and does not have details about how many hours were accrued. The latest start and
     * end date indicate the pay period until which the returned info is correct. If no records are stored for
     * the current year, null is returned.
     *
     * @param empId int - Employee id
     * @param year int - Year
     * @return AnnualAccrualUsage or null if not found
     */
    protected AnnualAccrualUsage getLatestAccUsageForYear(int empId, int year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("startDate", new LocalDate(year, 1, 1).toDate());
        AnnualAccrualUsage annAccUsage = null;
        try {
            annAccUsage = remoteNamedJdbc.queryForObject(LATEST_USAGE_SUMS, params, new AnnualAccrualUsageRowMapper());
        }
        catch (DataAccessException ex)  {
            logger.debug("Failed to get latest usage sums from PD23ATTEND for empId: {} in year: {}", empId, year);
        }
        return annAccUsage;
    }
}
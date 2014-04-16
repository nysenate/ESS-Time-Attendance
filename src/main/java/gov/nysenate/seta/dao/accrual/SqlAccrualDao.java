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
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.personnel.TransactionHistory;
import gov.nysenate.seta.model.personnel.TransactionRecord;
import gov.nysenate.seta.model.personnel.TransactionType;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.DateTime;
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

    protected static final Set<TransactionType> PAY_CODE_TYPES = new HashSet<>(Arrays.asList(APP, RTP, TYP));
    protected static final Set<TransactionType> MIN_HOUR_TYPES = new HashSet<>(Arrays.asList(APP, RTP, MIN));

    /** --- SQL Queries --- */

    protected static String LATEST_USAGE_SUMS =
        "SELECT \n" +
        "    MAX(DTBEGIN) AS LATEST_DTBEGIN, MAX(DTEND) AS LATEST_DTEND, " +
        "    SUM(NUWORKHRS) AS WORK_HRS, SUM(NUTRVHRS) AS TRV_HRS_USED, SUM(NUHOLHRS) AS HOL_HRS_USED, \n" +
        "    SUM(NUVACHRS) AS VAC_HRS_USED, SUM(NUPERHRS) AS PER_HRS_USED, SUM(NUEMPHRS) AS EMP_HRS_USED,\n" +
        "    SUM(NUFAMHRS) AS FAM_HRS_USED, SUM(NUMISCHRS) AS MISC_HRS_USED, SUM(NUTOTALHRS) AS TOTAL_HRS\n" +
        "FROM PD23ATTEND \n" +
        "WHERE NUXREFEM = :empId AND CDSTATUS = 'A'\n" +
        "AND DTBEGIN >= :startDate";

    protected static String GET_ANNUAL_ACCRUAL_SUMMARIES_SQL =
        "SELECT \n" +
        "    NUXREFEM, DTPERIODYEAR AS YEAR, DTCLOSE AS CLOSE_DATE, DTPERLSTPOST DTEND, " +
        "    NUWORKHRSTOT AS WORK_HRS, NUTRVHRSTOT AS TRV_HRS_USED, \n" +
        "    NUVACHRSTOT AS VAC_HRS_USED, NUVACHRSYTD AS VAC_HRS_ACCRUED, NUVACHRSBSD AS VAC_HRS_BANKED,\n" +
        "    NUPERHRSTOT AS PER_HRS_USED, NUPERHRSYTD AS PER_HRS_ACCRUED,\n" +
        "    NUEMPHRSTOT AS EMP_HRS_USED, NUFAMHRSTOT AS FAM_HRS_USED, NUEMPHRSYTD AS EMP_HRS_ACCRUED, \n" +
        "    NUEMPHRSBSD AS EMP_HRS_BANKED, NUHOLHRSTOT AS HOL_HRS_USED, NUMISCHRSTOT AS MISC_HRS_USED, \n" +
        "    NUPAYCTRYTD AS PAY_PERIODS_YTD, NUPAYCTRBSD AS PAY_PERIODS_BANKED\n" +
        "FROM PM23ATTEND WHERE NUXREFEM = :empId AND DTPERIODYEAR <= :endYear";

    protected static String GET_PERIOD_ACCRUAL_SUMMARY_SQL =
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

    protected static String GET_PERIOD_ACCRUAL_USAGE_SQL =
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
        int year = new DateTime(endDate).getYear();

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
        int year = new DateTime(payPeriodEndDate).getYear();

        PeriodAccrualSummary latestPeriodSum = periodSummaries.poll();
        if (periodSummaryIsCurrent(latestPeriodSum, payPeriod)) {
            latestPeriodSum.setPayPeriod(payPeriod);
            //return latestPeriodSum;
        }

        AnnualAccrualSummary activeAnnualSummary = getActiveAnnualSummaryInList(annualSummaries, payPeriodEndDate);

        Date gapStartDate = new DateTime(activeAnnualSummary.getEndDate()).plusDays(1).toDate();
        Date gapEndDate = new DateTime(payPeriod.getStartDate()).minusDays(1).toDate();

        TransactionHistory transHistDuringGap = getAccrualRelatedTransactions(empId, gapStartDate, gapEndDate);
        LinkedList<TransactionRecord> transRecsDuringGap = transHistDuringGap.getAllTransRecords(true);
        LinkedList<PeriodAccrualUsage> periodUsageRecs = getPeriodAccrualUsageRecords(empId, gapStartDate, gapEndDate);

        PayType basePayType = getLatestPayTypeForEmp(empId, activeAnnualSummary.getEndDate());
        BigDecimal baseMinHours = getLatestMinHoursForEmp(empId, activeAnnualSummary.getEndDate());

        logger.debug("!! Period Accrual Summary : {}", OutputUtils.toJson(latestPeriodSum));
        logger.debug("!! Annual Accrual Summary : {}", OutputUtils.toJson(activeAnnualSummary));
        logger.debug("!! Period Accrual Usage: {}", OutputUtils.toJson(periodUsageRecs));
        logger.debug("!! Base Pay Type: {}", basePayType);
        logger.debug("!! Base Min Hours: {}", baseMinHours);
        logger.debug("!! Pay Ctr as of {}: {}", activeAnnualSummary.getEndDate(), activeAnnualSummary.getPayPeriodsBanked());

        List<PayPeriod> gapPeriods = payPeriodDao.getPayPeriods(PayPeriodType.AF, gapStartDate, gapEndDate, true);
        Iterator<PayPeriod> gapPeriodIterator = gapPeriods.iterator();

        int payPeriodCounter = activeAnnualSummary.getPayPeriodsBanked();
        BigDecimal proratePercentage = baseMinHours.divide(new BigDecimal(1820));
        BigDecimal vacAccRate = AccrualRate.VACATION.getRate(payPeriodCounter, proratePercentage);
        BigDecimal sickAccRate = AccrualRate.SICK.getRate(payPeriodCounter, proratePercentage);
        BigDecimal sickAccrued = BigDecimal.ZERO;
        BigDecimal vacAccrued = BigDecimal.ZERO;

        Date minHourEffectDate = activeAnnualSummary.getEndDate();
        BigDecimal minHours = baseMinHours;

        TransactionRecord gapTransRecord = transRecsDuringGap.poll();
        PeriodAccrualUsage periodUsageRec = periodUsageRecs.poll();
        while (gapPeriodIterator.hasNext()) {
            PayPeriod gapPeriod = gapPeriodIterator.next();
            logger.debug("Iterating pay period: {}", OutputUtils.toJson(gapPeriod));

            while (gapTransRecord != null && gapTransRecord.getEffectDate().compareTo(gapPeriod.getEndDate()) <= 0) {
                if (MIN_HOUR_TYPES.contains(gapTransRecord.getTransType())) {
                    if (gapTransRecord.hasNonNullValue("NUMINTOTHRS")) {
                        minHourEffectDate = gapTransRecord.getEffectDate();
                        minHours = new BigDecimal(gapTransRecord.getValue("NUMINTOTHRS"));
                        logger.debug("!! Found new min hour type during {}: setting to: {}", minHourEffectDate, minHours);
                    }
                }
                gapTransRecord = transRecsDuringGap.poll();
            }


            if (periodUsageRec != null) {
                if (gapPeriod.equals(periodUsageRec.getPayPeriod())) {
                    logger.debug("Found usage rec!");
                    periodUsageRec = periodUsageRecs.poll();
                }
            }

            payPeriodCounter++;
            proratePercentage = minHours.divide(new BigDecimal(1820));
            vacAccRate = AccrualRate.VACATION.getRate(payPeriodCounter, proratePercentage);
            sickAccRate = AccrualRate.SICK.getRate(payPeriodCounter, proratePercentage);

            logger.debug("Prorate percentage: {} Curr Vac rate {} Curr Sick Rate {}", proratePercentage, vacAccRate, sickAccRate);

            sickAccrued = sickAccrued.add(sickAccRate);
            vacAccrued = vacAccrued.add(vacAccRate);

            logger.debug("Pay periods worked {} Vac accrued: {} Sick accrued {}", payPeriodCounter, vacAccrued, sickAccrued);

        }

        /*if (activeAccSum != null) {
            logger.debug("Base record date: {} type: {}", activeAccEndDate, activeAccSum.getClass());
            HashSet<TransactionType> types = new HashSet<>(Arrays.asList(APP, RTP, TYP, MIN, EMP));

            if (annualAccSum != null) {
                int payPeriodsWorked = annualAccSum.getPayPeriodsBanked();
                DateTime startFrom = new DateTime(annualAccSum.getEndDate()).plusDays(1);
                TransactionHistory transHist = empTransactionDao.getTransHistory(empId, types, annualAccSum.getEndDate());
                PayType basePayType = null;
                int baseMinHours;
                LinkedList<TransactionRecord> payRecs = transHist.getAllTransRecords(PAY_CODE_TYPES, false);
                basePayType = PayType.valueOf(payRecs.getFirst().getValueMap().get("CDPAYTYPE"));
                LinkedList<TransactionRecord> minHrsRecs = transHist.getAllTransRecords(MIN_HOUR_TYPES, false);
                baseMinHours = Integer.parseInt(minHrsRecs.getFirst().getValueMap().get("NUMINTOTHRS"));
                logger.info("Base pay type: {} min hours {} pay periods worked {}", basePayType, baseMinHours, payPeriodsWorked);

                TransactionHistory betweenHist = empTransactionDao.getTransHistory(empId, types, startFrom.toDate(), endDate);
                LinkedList<TransactionRecord> betweenRecs = betweenHist.getAllTransRecords(true);
                logger.info("Between recs: {}", OutputUtils.toJson(betweenRecs));

                TreeMap<Date, PayType> payTypeChanges = new TreeMap<>();
                TreeMap<Date, Integer> minHourChanges = new TreeMap<>();

                for (TransactionRecord rec : betweenRecs) {
                    if (PAY_CODE_TYPES.contains(rec.getTransType()) && rec.getValue("CDPAYTYPE") != null) {
                        payTypeChanges.put(rec.getEffectDate(), PayType.valueOf(rec.getValue("CDPAYTYPE")));
                    }
                    else if (MIN_HOUR_TYPES.contains(rec.getTransType()) && rec.getValue("NUMINTOTHRS") != null) {
                        minHourChanges.put(rec.getEffectDate(), Integer.parseInt(rec.getValue("NUMINTOTHRS")));
                    }
                }

                logger.debug("pay type changes: {}", OutputUtils.toJson(payTypeChanges));
                logger.debug("min hour changes: {}", OutputUtils.toJson(minHourChanges));

                LinkedList<PeriodAccrualUsage> accUsageRecords = getPeriodAccrualUsageRecords(empId, startFrom.toDate(), endDate);
                logger.debug("!! Period acc usage recs: {}", OutputUtils.toJson(accUsageRecords));

                if (!accUsageRecords.isEmpty()) {
                    DateTime lastAnnualSumDate = new DateTime(annualAccSum.getEndDate());
                    DateTime firstPeriodUsageDate = new DateTime(accUsageRecords.getFirst().getPayPeriod().getStartDate());
                    Duration yearAfter = new Duration(lastAnnualSumDate, lastAnnualSumDate.plusYears(1));
                    Duration timeBetween = new Duration(lastAnnualSumDate, firstPeriodUsageDate);
                    if (timeBetween.isLongerThan(yearAfter)) {
                        payPeriodsWorked = 0;
                    }
                }

                boolean isSplit = false;
                for (PeriodAccrualUsage accUsage : accUsageRecords) {
                    logger.debug("!! Period acc usage rec for date {}: {}", accUsage.getPayPeriod().getEndDate(), accUsage);
                    PayPeriod period = accUsage.getPayPeriod();
                    if (period.getNumDays() == 14) {
                        payPeriodsWorked++;
                    }
                    else {
                        if (isSplit) {
                            isSplit = false;
                        }
                        else {
                            isSplit = true;
                        }
                    }
                }
            }
        }  */
        return null;

    }

    /**
     * Gets the latest annual accrual record that has a posted end date that is before our given 'payPeriodEndDate'.
     * This is because
     */
    private AnnualAccrualSummary getActiveAnnualSummaryInList(Map<Integer, AnnualAccrualSummary> annualSummaries,
                                                              Date payPeriodEndDate) {
        AnnualAccrualSummary annualAccSum = null;
        if (!annualSummaries.isEmpty()) {
            Iterator<Integer> yearIterator = new TreeSet<>(annualSummaries.keySet()).descendingIterator();
            while (yearIterator.hasNext()) {
                annualAccSum = annualSummaries.get(yearIterator.next());
                if (annualAccSum.getEndDate().before(payPeriodEndDate)) {
                    break;
                }
            }
        }
        return annualAccSum;
    }

    /**
     * Checks if the period summary record has all the current information for the given pay period.
     */
    private boolean periodSummaryIsCurrent(PeriodAccrualSummary periodSummary, PayPeriod payPeriod) {
        Date prevPayPeriodEndDate = new DateTime(payPeriod.getStartDate()).minusDays(1).toDate();
        return (periodSummary != null && periodSummary.getEndDate().compareTo(prevPayPeriodEndDate) == 0);
    }

    /**
     * Get a TransactionHistory for an employee that contains any transactions relevant to accrual
     * processing logic.
     */
    private TransactionHistory getAccrualRelatedTransactions(int empId, Date startDate, Date endDate) {
        Set<TransactionType> types = new HashSet<>(Arrays.asList(APP, RTP, TYP, MIN, EMP));
        return empTransactionDao.getTransHistory(empId, types, startDate, endDate);
    }

    /**
     * Looks up the employee's pay type before or on 'latestDate'.
     */
    private PayType getLatestPayTypeForEmp(int empId, Date latestDate) throws AccrualException {
        Set<TransactionType> type = new LinkedHashSet<>(Arrays.asList(TYP, RTP, APP));
        TransactionHistory transHist = empTransactionDao.getTransHistory(empId, type, latestDate);
        LinkedList<TransactionRecord> recs = transHist.getAllTransRecords(false);
        while (transHist.hasRecords()) {
            TransactionRecord rec = recs.poll();
            if (rec.getValue("CDPAYTYPE") != null) {
                return PayType.valueOf(rec.getValue("CDPAYTYPE"));
            }
        }
        throw new AccrualException("Employee " + empId + " has no transactions that indicate their pay type " +
                                   "before or on " + latestDate);
    }

    /**
     * Looks up the employee's min hours before or on 'latestDate'.
     */
    private BigDecimal getLatestMinHoursForEmp(int empId, Date latestDate) throws AccrualException {
        Set<TransactionType> type = new LinkedHashSet<>(Arrays.asList(MIN, RTP, APP));
        TransactionHistory transHist = empTransactionDao.getTransHistory(empId, type, latestDate);
        LinkedList<TransactionRecord> recs = transHist.getAllTransRecords(false);
        while (!recs.isEmpty()) {
            TransactionRecord rec = recs.poll();
            if (rec.getValue("NUMINTOTHRS") != null) {
                return new BigDecimal(rec.getValue("NUMINTOTHRS"));
            }
        }
        throw new AccrualException("Employee " + empId + " has no transactions that indicate their minimum hours " +
                                   "before or on " + latestDate);
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
        params.addValue("startDate", new DateTime(year, 1, 1, 0, 0, 0).toDate());
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
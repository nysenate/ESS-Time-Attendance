package gov.nysenate.seta.service.accrual;

import com.google.common.collect.Range;
import gov.nysenate.common.LimitOffset;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.accrual.AccrualDao;
import gov.nysenate.seta.dao.attendance.TimeRecordDao;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.dao.transaction.EmpTransactionDao;
import gov.nysenate.seta.model.accrual.*;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Service layer for computing accrual information for an employee based on processed accrual
 * and employee transaction data in SFMS.
 */
@Service
public class EssAccrualComputeService implements AccrualComputeService
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualComputeService.class);

    @Autowired private AccrualDao accrualDao;
    @Autowired private PayPeriodDao periodDao;
    @Autowired private EmpTransactionDao empTransDao;
    @Autowired private TimeRecordDao timeRecordDao;

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public PeriodAccSummary getAccruals(int empId, PayPeriod payPeriod) throws AccrualException {
        return getAccruals(empId, Collections.singletonList(payPeriod)).get(payPeriod);
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<PayPeriod, PeriodAccSummary> getAccruals(int empId, List<PayPeriod> payPeriods) throws AccrualException {
        if (payPeriods.isEmpty()) {
            return new TreeMap<>();
        }
        TreeMap<PayPeriod, PeriodAccSummary> resultMap = new TreeMap<>();

        // Sorted set of the supplied pay periods
        TreeSet<PayPeriod> periodSet = new TreeSet<>(payPeriods);

        // Get period accrual records up to last pay period
        LocalDate beforeDate = periodSet.last().getEndDate().plusDays(1);
        TreeMap<PayPeriod, PeriodAccSummary> periodAccruals =
            accrualDao.getPeriodAccruals(empId, beforeDate, LimitOffset.ALL, SortOrder.ASC);

        // Annual accrual records, if necessary
        TreeMap<Integer, AnnualAccSummary> annualAccruals = null;

        // Emp Transaction History, if necessary
        TransactionHistory empTrans = null;

        for (PayPeriod p : periodSet) {
            if (periodAccruals.containsKey(p)) {
                resultMap.put(p, periodAccruals.get(p));
            }
            else {
                if (annualAccruals == null) {
                    annualAccruals = accrualDao.getAnnualAccruals(empId, periodSet.last().getYear());
                }
                if (empTrans == null) {
                    empTrans = empTransDao.getTransHistory(empId, EmpTransDaoOption.INITIALIZE_AS_APP);
                }
                Map.Entry<PayPeriod, PeriodAccSummary> periodAccRecord = periodAccruals.lowerEntry(p);
                Optional<PeriodAccSummary> optPeriodAccRecord =
                    (periodAccRecord != null) ? Optional.of(periodAccRecord.getValue()) : Optional.empty();
                resultMap.put(p, computeAccruals(empId, p, empTrans, optPeriodAccRecord, annualAccruals.get(p.getYear())));
            }
        }
        return resultMap;
    }

    /**
     *
     * @param empId int - Employee id
     * @param payPeriod PayPeriod - The pay period we want to compute accruals for
     * @param transHistory TransactionHistory - The full transaction history for the employee
     * @param periodAccSum Optional<PeriodAccSummary> - The latest period acc summary record prior to this pay period
     * @param annualAcc AnnualAccSummary - The annual accrual record for this pay period's year
     * @return PeriodAccSummary
     * @throws AccrualException - If accruals cannot be computed with the supplied data.
     */
    protected PeriodAccSummary computeAccruals(int empId, PayPeriod payPeriod, TransactionHistory transHistory,
                                               Optional<PeriodAccSummary> periodAccSum,
                                               AnnualAccSummary annualAcc) throws AccrualException {
        verifyValidPayPeriod(payPeriod);
        LocalDate periodStartDate = payPeriod.getStartDate();

        // If a period accrual usage record exists for the previous pay period, most of the work is done.
        if (periodAccSum.isPresent() && periodAccSum.get().getEndDate().isEqual(periodStartDate.minusDays(1))) {
            logger.debug("Accrual summary found for previous pay period: {}", periodAccSum.get().getEndDate());
            return periodAccSum.get();
        }

        // If no PM23ATTEND record exists, we cannot compute accruals. For new employees a record will exist since
        // it is created initially by personnel.
        if (annualAcc == null) {
            throw new AccrualException(empId, AccrualExceptionType.NO_ACTIVE_ANNUAL_RECORD_FOUND);
        }

        // Compute an initial accrual state that is effective up to the DTPERLSPOST date from the annual accrual
        // record.
        AccrualState accrualState = new AccrualState(annualAcc);
        Range<LocalDate> initialRange = Range.atMost(accrualState.getEndDate());

        // Set the expected YTD hours from the last PD23ACCUSAGE record
        if (periodAccSum.isPresent()) {
            accrualState.setYtdHoursExpected(periodAccSum.get().getExpectedTotalHours());
        }
        else {
            accrualState.setYtdHoursExpected(BigDecimal.ZERO);
        }

        accrualState.setEmployeeActive(transHistory.getEffectiveEmpStatus(initialRange).lastEntry().getValue());
        accrualState.setPayType(transHistory.getEffectivePayTypes(initialRange).lastEntry().getValue());
        accrualState.setMinTotalHours(transHistory.getEffectiveMinHours(initialRange).lastEntry().getValue());
        accrualState.computeRates();

        // Generate a list of all the pay periods between the period immediately following the DTPERLSPOST and
        // before the pay period we are trying to compute available accruals for. We will call these the accrual
        // gap periods.
        Range<LocalDate> gapDateRange = Range.open(accrualState.getEndDate(), periodStartDate);
        LinkedList<PayPeriod> gapPeriods = new LinkedList<>(
            periodDao.getPayPeriods(PayPeriodType.AF, gapDateRange, SortOrder.ASC));

        // Retrieve all PD23ATTEND records that are available during the accrual gap.
        TreeMap<PayPeriod, PeriodAccUsage> periodUsages = accrualDao.getPeriodAccrualUsages(empId, gapDateRange);

        // Retrieve all time records for all pay periods between the one following the last retrieved PD23ATTEND
        // record and the last accrual gap period.
        LocalDate fetchTimeRecordsAfter = (periodUsages.isEmpty()) ?
                                          gapDateRange.lowerEndpoint() : periodUsages.lastKey().getEndDate();
        List<TimeRecord> timeRecords =
            timeRecordDao.getRecordsDuring(empId, Range.open(fetchTimeRecordsAfter, periodStartDate));

        for (PayPeriod gapPeriod : gapPeriods) {
            computeGapPeriodAccruals(gapPeriod, accrualState, transHistory, timeRecords, periodUsages);
        }
        PayPeriod refPeriod = (periodAccSum.isPresent()) ? periodAccSum.get().getRefPayPeriod() : gapPeriods.getFirst();
        return accrualState.toPeriodAccrualSummary(refPeriod, payPeriod);
    }

    /**
     *
     * @param gapPeriod PayPeriod
     * @param accrualState AccrualState
     * @param transHistory TransactionHistory
     * @param timeRecords List<TimeRecord>
     * @param periodUsages TreeMap<PayPeriod, PeriodAccUsage>
     */
    private void computeGapPeriodAccruals(PayPeriod gapPeriod, AccrualState accrualState, TransactionHistory transHistory,
                                          List<TimeRecord> timeRecords, TreeMap<PayPeriod, PeriodAccUsage> periodUsages) {
        Range<LocalDate> gapPeriodRange = gapPeriod.getDateRange();
        if (accrualState.isEmployeeActive()) {
            TreeMap<LocalDate, PayType> payTypes = transHistory.getEffectivePayTypes(gapPeriodRange);
            if (!payTypes.isEmpty()) {
                accrualState.setPayType(payTypes.lastEntry().getValue());
            }
            TreeMap<LocalDate, BigDecimal> minHours = transHistory.getEffectiveMinHours(gapPeriodRange);
            if (!minHours.isEmpty()) {
                accrualState.setMinTotalHours(minHours.lastEntry().getValue());
            }
            // If the employee is currently a RA or SA
            if (!accrualState.getPayType().equals(PayType.TE)) {
                // If pay period is start of new year perform necessary adjustments to the accruals.
                if (gapPeriod.isStartOfYearSplit()) {
                    accrualState.applyYearRollover();
                }
                // Set accrual usage from matching PD23ATTEND record.
                if (periodUsages.containsKey(gapPeriod)) {
                    accrualState.addUsage(periodUsages.get(gapPeriod));
                }
                // Otherwise check if there is a time record to apply accrual usage from.
                else if (!timeRecords.isEmpty() && timeRecords.get(0).getPayPeriod().equals(gapPeriod)) {
                    accrualState.addUsage(timeRecords.get(0).getPeriodAccUsage());
                    timeRecords.remove(0);
                }
                // As long as this is a valid accrual period, increment the accruals.
                if (!gapPeriod.isEndOfYearSplit()) {
                    accrualState.incrementPayPeriodCount();
                    accrualState.computeRates();
                    accrualState.incrementAccrualsEarned();
                }
                // Adjust the year to date hours expected
                accrualState.incrementYtdHoursExpected(gapPeriod);
            }
        }
        // Set the employment status if changed.
        TreeMap<LocalDate, Boolean> empStatus = transHistory.getEffectiveEmpStatus(gapPeriodRange);
        if (!empStatus.isEmpty()) {
            accrualState.setEmployeeActive(empStatus.lastEntry().getValue());
        }
    }

    /** --- Internal Methods --- */

    private void verifyValidPayPeriod(PayPeriod payPeriod) {
        if (payPeriod == null) {
            throw new IllegalArgumentException("Supplied payPeriod cannot be null.");
        }
        else if (!payPeriod.getType().equals(PayPeriodType.AF)) {
            throw new IllegalArgumentException("Supplied payPeriod must be of type AF (Attendance Fiscal).");
        }
    }
}
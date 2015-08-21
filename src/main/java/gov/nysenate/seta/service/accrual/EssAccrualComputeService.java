package gov.nysenate.seta.service.accrual;

import com.google.common.collect.Range;
import gov.nysenate.common.LimitOffset;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.accrual.AccrualDao;
import gov.nysenate.seta.dao.attendance.TimeRecordDao;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.accrual.*;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.base.CachingService;
import gov.nysenate.seta.service.base.SqlDaoBackedService;
import gov.nysenate.seta.service.period.PayPeriodService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for computing accrual information for an employee based on processed accrual
 * and employee transaction data in SFMS.
 */
@Service
public class EssAccrualComputeService extends SqlDaoBackedService implements AccrualComputeService
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualComputeService.class);

    @Autowired private PayPeriodService payPeriodService;
    @Autowired private EmpTransactionService empTransService;

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

        // Filter out the pay periods that we already have PD23ACCUSAGE records for.
        Iterator<PayPeriod> periodItr = periodSet.iterator();
        while (periodItr.hasNext()) {
            PayPeriod currPeriod = periodItr.next();
            verifyValidPayPeriod(currPeriod);
            if (periodAccruals.containsKey(currPeriod)) {
                resultMap.put(currPeriod, periodAccruals.get(currPeriod));
                periodItr.remove();
            }
        }

        if (!periodSet.isEmpty()) {
            PayPeriod lastPeriod = periodSet.last();
            TreeMap<Integer, AnnualAccSummary> annualAcc = accrualDao.getAnnualAccruals(empId, lastPeriod.getYear());
            if (annualAcc.isEmpty()) {
                throw new AccrualException(empId, AccrualExceptionType.NO_ACTIVE_ANNUAL_RECORD_FOUND);
            }
            LocalDate fromDate = com.google.common.base.Objects.firstNonNull(
                annualAcc.lastEntry().getValue().getEndDate(), annualAcc.lastEntry().getValue().getContServiceDate());

            if (fromDate.isBefore(lastPeriod.getEndDate())) {
                Range<LocalDate> periodRange = Range.closed(fromDate, lastPeriod.getEndDate());
                List<PayPeriod> unMatchedPeriods = payPeriodService.getPayPeriods(PayPeriodType.AF, periodRange, SortOrder.ASC);
                TransactionHistory empTrans = empTransService.getTransHistory(empId);
                TreeMap<PayPeriod, PeriodAccUsage> periodUsages = accrualDao.getPeriodAccrualUsages(empId, periodRange);
                List<TimeRecord> timeRecords = timeRecordDao.getRecordsDuring(empId, periodRange);

                Map.Entry<PayPeriod, PeriodAccSummary> periodAccRecord = periodAccruals.lowerEntry(lastPeriod);
                Optional<PeriodAccSummary> optPeriodAccRecord =
                        (periodAccRecord != null) ? Optional.of(periodAccRecord.getValue()) : Optional.empty();

                AccrualState accrualState = computeAccrualState(empTrans, optPeriodAccRecord, annualAcc.get(lastPeriod.getYear()));

                // Generate a list of all the pay periods between the period immediately following the DTPERLSPOST and
                // before the pay period we are trying to compute available accruals for. We will call these the accrual
                // gap periods.
                Range<LocalDate> gapDateRange = Range.openClosed(accrualState.getEndDate(), lastPeriod.getEndDate());
                LinkedList<PayPeriod> gapPeriods = new LinkedList<>(unMatchedPeriods.stream()
                        .filter(p -> gapDateRange.encloses(p.getDateRange()))
                        .collect(Collectors.toList()));
                PayPeriod refPeriod = (optPeriodAccRecord.isPresent()) ? optPeriodAccRecord.get().getRefPayPeriod()
                        : gapPeriods.getFirst(); // FIXME?
                for (PayPeriod gapPeriod : gapPeriods) {
                    computeGapPeriodAccruals(gapPeriod, accrualState, empTrans, timeRecords, periodUsages);
                    if (periodSet.contains(gapPeriod)) {
                        resultMap.put(gapPeriod, accrualState.toPeriodAccrualSummary(refPeriod, gapPeriod));
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * Compute an initial accrual state that is effective up to the DTPERLSPOST date from the annual accrual record.
     * @param transHistory TransactionHistory
     * @param periodAccSum Optional<PeriodAccSummary>
     * @param annualAcc AnnualAccSummary
     * @return AccrualState
     */
    private AccrualState computeAccrualState(TransactionHistory transHistory, Optional<PeriodAccSummary> periodAccSum,
                                             AnnualAccSummary annualAcc) {
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
        return accrualState;
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
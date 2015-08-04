package gov.nysenate.seta.service.accrual;

import com.google.common.collect.Range;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import gov.nysenate.common.OutputUtils;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

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
        verifyValidPayPeriod(payPeriod);
        LocalDate periodStartDate = payPeriod.getStartDate();
        TreeMap<PayPeriod, PeriodAccSummary> accSummMap =
            accrualDao.getPeriodAccruals(empId, periodStartDate.getYear(), periodStartDate);

        // If a period accrual usage record exists for the previous pay period, most of the work is done.
        if (!accSummMap.isEmpty() && accSummMap.lastKey().getEndDate().isEqual(periodStartDate.minusDays(1))) {
            logger.debug("Accrual summary found for previous pay period: {}", accSummMap.lastKey());
            return accSummMap.lastEntry().getValue();
        }

        // Retrieve the latest PM23ATTEND record since it will contain the number of pay periods worked up to DTPERLSPOST.
        // For new employees a record will exist since it is created initially by personnel. If no PM23ATTEND record exists,
        // we cannot compute accruals.
        TreeMap<Integer, AnnualAccSummary> annualAccruals =
                accrualDao.getAnnualAccruals(empId, payPeriod.getEndDate().getYear());
        if (annualAccruals.isEmpty()) {
            throw new AccrualException(empId, AccrualExceptionType.NO_ACTIVE_ANNUAL_RECORD_FOUND);
        }

        // Fetch the full transaction history
        TransactionHistory transHistory = empTransDao.getTransHistory(empId, EmpTransDaoOption.INITIALIZE_AS_APP);

        // Compute an initial accrual state that is effective up to the DTPERLSPOST date from the annual accrual
        // record.
        AnnualAccSummary annualRec = annualAccruals.lastEntry().getValue();
        AccrualState accrualState = new AccrualState(annualRec);
        Range<LocalDate> initialRange = Range.atMost(accrualState.getEndDate());
        // FIXME
        accrualState.setYtdHoursExpected(new BigDecimal(0));
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
        return accrualState.toPeriodAccrualSummary(gapPeriods.getLast(), payPeriod);
    }

    @Override
    public TreeMap<PayPeriod, PeriodAccSummary> getAccruals(int empId, Range<PayPeriod> payPeriodRange) throws AccrualException {
        PayPeriod startPeriod = payPeriodRange.lowerEndpoint();
        PayPeriod endPeriod = payPeriodRange.upperEndpoint();
//        Map<Integer, >
        List<Integer> years = new ArrayList<>();
        Integer year = startPeriod.getEndDate().getYear();
        do {
            years.add(year);
            year++;
        }
        while (year <= endPeriod.getEndDate().getYear());
        return null;
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
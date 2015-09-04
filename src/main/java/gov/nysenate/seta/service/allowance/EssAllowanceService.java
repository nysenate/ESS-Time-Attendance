package gov.nysenate.seta.service.allowance;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.model.allowances.HourlyWorkPayment;
import gov.nysenate.seta.model.allowances.AllowanceUsage;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.payroll.SalaryRec;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.attendance.TimeRecordService;
import gov.nysenate.seta.service.period.PayPeriodService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.seta.model.period.PayPeriodType.*;

@Service
public class EssAllowanceService implements AllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(EssAllowanceService.class);

    @Autowired EmpTransactionService transService;
    @Autowired PayPeriodService periodService;
    @Autowired TimeRecordService tRecS;

    /** {@inheritDoc} */
    @Override
    public AllowanceUsage getAllowanceUsage(int empId, int year) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        RangeMap<LocalDate, SalaryRec> salaryRecs = transHistory.getSalaryRecs();

        BigDecimal baseHoursUsed = BigDecimal.ZERO;
        BigDecimal baseMoneyUsed = BigDecimal.ZERO;
        List<HourlyWorkPayment> payments = transHistory.getHourlyPayments(year);
        Range<LocalDate> yearDateRange = Range.closed(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        Set<PayPeriod> unpaidPeriods = new HashSet<>(periodService.getPayPeriods(AF, yearDateRange, SortOrder.NONE));
        // Add up hourly work payments to get the total hours/money paid for the year
        for (HourlyWorkPayment payment : payments) {
            baseHoursUsed = baseHoursUsed.add(payment.getHoursWorkedForYear(year)); //FIXME getHoursWorkedForYear doesn't work atm
            baseMoneyUsed = baseMoneyUsed.add(payment.getMoneyPaidForYear(year));
            unpaidPeriods.removeAll(
                    periodService.getPayPeriods(AF, payment.getWorkingRange(), SortOrder.NONE));
        }

        BigDecimal recordHoursUsed = BigDecimal.ZERO;
        BigDecimal recordMoneyUsed = BigDecimal.ZERO;
        List<TimeRecord> timeRecords =
                tRecS.getTimeRecords(Collections.singleton(empId), unpaidPeriods, EnumSet.allOf(TimeRecordStatus.class), false);
        // Add up hours and calculated payment for time records that have not been paid out yet
        for (TimeRecord wreckerd : timeRecords) {
            for (TimeEntry entry : wreckerd.getTimeEntries()) {
                BigDecimal workHours = entry.getWorkHours().orElse(BigDecimal.ZERO);
                SalaryRec salaryForEntry = salaryRecs.get(entry.getDate());
                if (salaryForEntry != null && salaryForEntry.getPayType() == PayType.TE) {
                    recordHoursUsed = recordHoursUsed.add(workHours);
                    recordMoneyUsed = recordMoneyUsed.add(workHours.multiply(salaryForEntry.getSalaryRate()));
                }
            }
        }

        return new AllowanceUsage(
                empId, year, baseHoursUsed, baseMoneyUsed, recordHoursUsed, recordMoneyUsed
        );
    }

}

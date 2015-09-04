package gov.nysenate.seta.model.payroll;

import com.google.common.collect.Range;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalaryRec {

    /** The amount paid to the employee per pay period/hour/year */
    private BigDecimal salaryRate;

    /** Specifies the time unit that this salary is tracked by e.g. biweekly/hourly/yearly */
    private PayType payType;

    /** The start and end points for the range of dates for which this salary is in effect */
    private LocalDate effectDate;
    private LocalDate endDate;

    public SalaryRec(BigDecimal salaryRate, PayType payType, LocalDate effectDate, LocalDate endDate) {
        this.salaryRate = salaryRate;
        this.payType = payType;
        this.effectDate = effectDate;
        this.endDate = endDate;
    }

    /** --- Functional Getters --- */

    public Range<LocalDate> getEffectiveRange() {
        return Range.closed(effectDate, endDate);
    }

    /** --- Getters --- */

    public BigDecimal getSalaryRate() {
        return salaryRate;
    }

    public PayType getPayType() {
        return payType;
    }

    public LocalDate getEffectDate() {
        return effectDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}

package gov.nysenate.seta.model.period;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import gov.nysenate.seta.model.attendance.TimeRecord;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Objects;

/**
 * Contains the date information for a single pay period.
 */
public class PayPeriod implements Comparable<PayPeriod>
{
    /** The expected number of days in a full pay period. */
    public static int DEFAULT_PAY_PERIOD_DAYS = 14;

    /** The type of pay period. The one we deal with most is Attendance Fiscal (AF). */
    protected PayPeriodType type;

    /** The starting date of the pay period. */
    protected LocalDate startDate;

    /** The ending date of the pay period. */
    protected LocalDate endDate;

    /** A number that is attributed to the pay period that typically rolls over after a fiscal year. */
    protected int payPeriodNum;

    /** Indicates if this pay period is set as active in the backing store. */
    protected boolean active;

    /** --- Constructors --- */

    public PayPeriod() {}

    public PayPeriod(PayPeriodType type, LocalDate startDate, LocalDate endDate, int payPeriodNum, boolean active) {
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.payPeriodNum = payPeriodNum;
        this.active = active;
    }

    /** --- Functions --- */

    public boolean isEnclosedBy(Collection<TimeRecord> records) {
        if (records != null) {
            RangeSet<LocalDate> rangeSet = TreeRangeSet.create();
            records.forEach(rec -> rangeSet.add(rec.getDateRange()));
            return rangeSet.encloses(this.getDateRange());
        }
        return false;
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Returns the number of days between the start date and end date (inclusive) of this pay period.
     * @return int
     */
    public int getNumDaysInPeriod() {
        if (startDate != null && endDate != null) {
            return Period.between(startDate, endDate.plusDays(1)).getDays();
        }
        throw new IllegalStateException("Start date and/or end date is null. " +
                                        "Cannot compute number of pay period days");
    }

    /**
     * Returns the number of week days between the start date and end date (inclusive) of this pay period.
     * @return int
     */
    public int getNumWeekDaysInPeriod() {
        if (startDate != null && endDate != null) {
            LocalDate date = startDate;
            int workDays = 0;
            while (!date.isAfter(endDate)) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                if (!dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY)) {
                    workDays++;
                }
                date = date.plusDays(1);
            }
            return workDays;
        }
        throw new IllegalStateException("Start date and/or end date is null. " +
                                        "Cannot compute number of pay period work days");
    }

    /**
     * Indicates if this pay period is an end of year split pay period, which is basically a pay period
     * that gets truncated to have fewer days due to some pay period types (like AF) not rolling over years.
     *
     * @return boolean - true if this marks an end of year split pay period.
     */
    public boolean isEndOfYearSplit() {
        return endDate.getDayOfYear() == endDate.lengthOfYear() && getNumDaysInPeriod() != DEFAULT_PAY_PERIOD_DAYS;
    }

    /**
     * Indicates if this pay period is a start of year split pay period, which is basically a pay period
     * that gets truncated to have fewer days due to some pay period types (like AF) not rolling over years.
     *
     * @return boolean - true if this marks a start of year split pay period.
     */
    public boolean isStartOfYearSplit() {
        return startDate.getDayOfYear() == 1 && getNumDaysInPeriod() != DEFAULT_PAY_PERIOD_DAYS;
    }

    public Range<LocalDate> getDateRange() {
        if (startDate != null && endDate != null) {
            return Range.closedOpen(startDate, endDate.plusDays(1));
//            return Range.closed(startDate, endDate);
        }
        throw new IllegalArgumentException("Cannot return date range since start and/or end dates are null!");
    }

    /**
     * Returns the year this pay period ends on.
     * @return int
     */
    public int getYear() {
        return endDate.getYear();
    }

    /** --- Overrides --- */

    @Override
    public int hashCode() {
        return Objects.hash(type, startDate, endDate, payPeriodNum, active);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final PayPeriod other = (PayPeriod) obj;
        return Objects.equals(this.type, other.type) &&
               Objects.equals(this.startDate, other.startDate) &&
               Objects.equals(this.endDate, other.endDate) &&
               Objects.equals(this.payPeriodNum, other.payPeriodNum) &&
               Objects.equals(this.active, other.active);
    }

    @Override
    public String toString() {
        return "PayPeriod{" + "type=" + type + ", startDate=" + startDate + ", endDate=" + endDate + ", payPeriodNum=" +
                payPeriodNum + ", active=" + active + '}';
    }

    @Override
    public int compareTo(PayPeriod o) {
        return ComparisonChain.start()
            .compare(this.getStartDate(), o.getStartDate())
            .compare(this.getEndDate(), o.getEndDate())
            .compare(this.isActive(), o.isActive())
            .result();
    }

    /** --- Basic Getters/Setters --- */

    public PayPeriodType getType() {
        return type;
    }

    public void setType(PayPeriodType type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getPayPeriodNum() {
        return payPeriodNum;
    }

    public void setPayPeriodNum(int payPeriodNum) {
        this.payPeriodNum = payPeriodNum;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
package gov.nysenate.seta.model.period;


import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Contains the date information for a single pay period.
 */
public class PayPeriod
{
    public static int PAY_PERIOD_LENGTH =

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

    /** --- Functional Getters/Setters --- */

    /**
     * Returns the number of days between the start date (inclusive) and end date (inclusive)
     * of this pay period.
     *
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
     * Indicates if this pay period is an end of year split pay period, which is basically a pay period
     * that gets truncated to less than 14 days due to some pay period types (like AF) not rolling over years.
     *
     * @return boolean - true if this marks an end of year split pay period.
     */
    public boolean isEndOfYearSplit() {
        return endDate.getDayOfYear() == endDate.lengthOfYear() && getNumDaysInPeriod() != 14;
    }

    /**
     * Indicates if this pay period is a start of year split pay period, which is basically a pay period
     * that gets truncated to less than 14 days due to some pay period types (like AF) not rolling over years.
     *
     * @return boolean - true if this marks a start of year split pay period.
     */
    public boolean isStartOfYearSplit() {
        return startDate.getDayOfYear() == 1 && getNumDaysInPeriod() != 14;
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

    /** --- Basic Getters/Setters --- */


}
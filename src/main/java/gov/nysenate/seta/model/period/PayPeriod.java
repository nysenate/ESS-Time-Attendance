package gov.nysenate.seta.model.period;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Date;

/**
 * Contains the date information for a single pay period.
 */
public class PayPeriod
{
    protected PayPeriodType type;
    protected Date startDate;
    protected Date endDate;
    protected int payPeriodNum;
    protected boolean active;

    public PayPeriod() {}

    /** --- Functional Getters/Setters --- */

    /**
     * Returns the number of days in the pay period.
     * @return long
     */
    public long getNumDays() {
        if (startDate != null && endDate != null) {
            Duration duration = new Duration(new DateTime(startDate), new DateTime(endDate).plusDays(1));
            long hours = duration.getStandardHours();
            /** Handle DST edge case: one extra hour during rollover */
            if (hours % 24 == 1) {
                hours -= 1;
            }
            /** Handle DST edge case: one less hour during rollover */
            else if (hours % 24 == 23) {
                hours += 1;
            }
            return hours / 24;
        }
        throw new IllegalStateException("Start date and/or end date is null. " +
                                        "Cannot compute number of pay period days");
    }

    /**
     * Indicates if pay period is the split that sometimes occurs at the end of the year.
     */
    public boolean isEndOfYearSplit() {
        LocalDate localDate = new LocalDate(endDate);
        return localDate.getMonthOfYear() == 12 && localDate.getDayOfMonth() == 31 && getNumDays() != 14;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PayPeriod payPeriod = (PayPeriod) o;
        if (active != payPeriod.active) return false;
        if (payPeriodNum != payPeriod.payPeriodNum) return false;
        if (!endDate.equals(payPeriod.endDate)) return false;
        if (!startDate.equals(payPeriod.startDate)) return false;
        if (type != payPeriod.type) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        result = 31 * result + payPeriodNum;
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PayPeriod{" + "type=" + type + ", startDate=" + startDate + ", endDate=" + endDate + ", payPeriodNum=" +
                payPeriodNum + ", active=" + active + '}';
    }

    /** --- Basic Getters/Setters --- */

    public PayPeriodType getType() {
        return type;
    }

    public void setType(PayPeriodType type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
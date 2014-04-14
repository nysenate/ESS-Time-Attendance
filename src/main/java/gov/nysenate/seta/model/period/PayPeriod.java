package gov.nysenate.seta.model.period;

import org.joda.time.DateTime;
import org.joda.time.Duration;

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

    /** Functional Getters/Setters */

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

    /** Basic Getters/Setters */

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
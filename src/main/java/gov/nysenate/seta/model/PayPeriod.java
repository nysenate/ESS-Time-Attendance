package gov.nysenate.seta.model;

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
package gov.nysenate.seta.model;

import java.util.Date;

public class PayPeriod
{
    protected int payPeriodId;
    protected PayPeriodType type;
    protected Date startDate;
    protected Date endDate;
    protected String payPeriodCode;
    protected boolean active;

    public PayPeriod() {}

    public int getPayPeriodId() {
        return payPeriodId;
    }

    public void setPayPeriodId(int payPeriodId) {
        this.payPeriodId = payPeriodId;
    }

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

    public String getPayPeriodCode() {
        return payPeriodCode;
    }

    public void setPayPeriodCode(String payPeriodCode) {
        this.payPeriodCode = payPeriodCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

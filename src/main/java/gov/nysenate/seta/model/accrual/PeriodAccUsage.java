package gov.nysenate.seta.model.accrual;

import gov.nysenate.seta.model.period.PayPeriod;

public class PeriodAccUsage extends AccrualUsage
{
    int year;
    PayPeriod payPeriod;

    public PeriodAccUsage() {}

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
    }
}

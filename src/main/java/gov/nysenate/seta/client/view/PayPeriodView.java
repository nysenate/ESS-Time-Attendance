package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.period.PayPeriod;

import java.time.LocalDate;

public class PayPeriodView implements ViewObject
{
    protected int payPeriodNum;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected String type;

    public PayPeriodView(PayPeriod payPeriod) {
        if (payPeriod != null) {
            this.startDate = payPeriod.getStartDate();
            this.endDate = payPeriod.getEndDate();
            this.payPeriodNum = payPeriod.getPayPeriodNum();
            this.type = payPeriod.getType().toString();
        }
    }

    @Override
    public String getViewType() {
        return "pay period";
    }

    public int getPayPeriodNum() {
        return payPeriodNum;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getType() {
        return type;
    }
}
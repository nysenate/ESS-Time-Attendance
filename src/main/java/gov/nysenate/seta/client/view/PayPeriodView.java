package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.period.PayPeriod;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement(name = "payPeriod")
public class PayPeriodView implements ViewObject
{
    @XmlElement public int payPeriodNum;
    @XmlElement public LocalDate startDate;
    @XmlElement public LocalDate endDate;
    @XmlElement public String type;
    @XmlElement public int numDays;
    @XmlElement public boolean startYearSplit;
    @XmlElement public boolean endYearSplit;

    public PayPeriodView(PayPeriod payPeriod) {
        if (payPeriod != null) {
            this.startDate = payPeriod.getStartDate();
            this.endDate = payPeriod.getEndDate();
            this.payPeriodNum = payPeriod.getPayPeriodNum();
            this.type = payPeriod.getType().toString();
            this.numDays = payPeriod.getNumDaysInPeriod();
            this.startYearSplit = payPeriod.isStartOfYearSplit();
            this.endYearSplit = payPeriod.isEndOfYearSplit();
        }
    }

    @Override
    public String getViewType() {
        return "pay period";
    }
}
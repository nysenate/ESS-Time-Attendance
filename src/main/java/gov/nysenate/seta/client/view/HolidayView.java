package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.payroll.Holiday;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement(name = "holiday")
public class HolidayView implements ViewObject
{
    protected LocalDate date;
    protected String name;
    protected boolean unofficial;

    public HolidayView(Holiday holiday) {
        if (holiday != null) {
            this.date = holiday.getDate();
            this.name = holiday.getName();
            this.unofficial = holiday.isQuestionable();
        }
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "holiday";
    }

    @XmlElement
    public LocalDate getDate() {
        return date;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public boolean isUnofficial() {
        return unofficial;
    }
}
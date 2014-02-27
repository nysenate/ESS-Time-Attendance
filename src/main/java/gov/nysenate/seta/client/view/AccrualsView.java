package gov.nysenate.seta.client.view;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Accruals")
public class AccrualsView
{
    @XmlElement
    protected int sick;

    @XmlElement
    protected int personal;

    @XmlElement
    protected int vacation;

    @XmlElement
    protected int holiday;

    @XmlElement
    protected int ytdServiceActual;

    @XmlElement
    protected int ytdServiceExpected;

    public AccrualsView() {
    }

    /** TODO: Constructor */


    public int getSick() {
        return sick;
    }

    public int getPersonal() {
        return personal;
    }

    public int getVacation() {
        return vacation;
    }

    public int getHoliday() {
        return holiday;
    }

    public int getYtdServiceActual() {
        return ytdServiceActual;
    }

    public int getYtdServiceExpected() {
        return ytdServiceExpected;
    }
}

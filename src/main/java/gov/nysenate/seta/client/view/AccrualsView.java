package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "Accruals")
public class AccrualsView implements ViewObject
{
    protected PayPeriodView payPeriod;
    protected boolean computed;

    protected BigDecimal sickAccruedYtd;
    protected BigDecimal personalAccruedYtd;
    protected BigDecimal vacationAccruedYtd;
    protected BigDecimal serviceYtd;
    protected BigDecimal serviceYtdExpected;

    protected BigDecimal sickBanked;
    protected BigDecimal vacationBanked;

    protected BigDecimal empSickUsed;
    protected BigDecimal famSickUsed;
    protected BigDecimal personalUsed;
    protected BigDecimal vacationUsed;
    protected BigDecimal holidayUsed;
    protected BigDecimal miscUsed;

    protected BigDecimal vacationRate;
    protected BigDecimal sickRate;

    /** --- Constructors --- */

    public AccrualsView(PeriodAccSummary pac) {
        if (pac != null) {
            this.payPeriod = new PayPeriodView(pac.getPayPeriod());
            this.computed = pac.isComputed();
            this.sickAccruedYtd = pac.getEmpHoursAccrued();
            this.personalAccruedYtd = pac.getPerHoursAccrued();
            this.vacationAccruedYtd = pac.getVacHoursAccrued();
            this.serviceYtd = pac.getTotalHoursYtd();
            this.serviceYtdExpected = pac.getExpectedTotalHours();
            this.sickBanked = pac.getEmpHoursBanked();
            this.vacationBanked = pac.getVacHoursBanked();
            this.empSickUsed = pac.getEmpHoursUsed();
            this.famSickUsed = pac.getFamHoursUsed();
            this.personalUsed = pac.getPerHoursUsed();
            this.vacationUsed = pac.getVacHoursUsed();
            this.holidayUsed = pac.getHolHoursUsed();
            this.miscUsed = pac.getMiscHoursUsed();
            this.vacationRate = pac.getVacRate();
            this.sickRate = pac.getSickRate();
        }
    }

    /** --- Functional Getters --- */

    public BigDecimal getVacationAvailable() {
        return this.vacationAccruedYtd.add(this.vacationBanked).subtract(this.vacationUsed);
    }

    public BigDecimal getPersonalAvailable() {
        return this.personalAccruedYtd.subtract(this.personalUsed);
    }

    public BigDecimal getSickAvailable() {
        return this.sickAccruedYtd.add(this.sickBanked).subtract(this.empSickUsed).subtract(this.famSickUsed);
    }

    /** --- Basic Getters --- */

    public PayPeriodView getPayPeriod() {
        return payPeriod;
    }

    public boolean isComputed() {
        return computed;
    }

    public BigDecimal getSickAccruedYtd() {
        return sickAccruedYtd;
    }

    public BigDecimal getPersonalAccruedYtd() {
        return personalAccruedYtd;
    }

    public BigDecimal getVacationAccruedYtd() {
        return vacationAccruedYtd;
    }

    public BigDecimal getServiceYtd() {
        return serviceYtd;
    }

    public BigDecimal getServiceYtdExpected() {
        return serviceYtdExpected;
    }

    public BigDecimal getSickBanked() {
        return sickBanked;
    }

    public BigDecimal getVacationBanked() {
        return vacationBanked;
    }

    public BigDecimal getEmpSickUsed() {
        return empSickUsed;
    }

    public BigDecimal getFamSickUsed() {
        return famSickUsed;
    }

    public BigDecimal getPersonalUsed() {
        return personalUsed;
    }

    public BigDecimal getVacationUsed() {
        return vacationUsed;
    }

    public BigDecimal getHolidayUsed() {
        return holidayUsed;
    }

    public BigDecimal getMiscUsed() {
        return miscUsed;
    }

    public BigDecimal getVacationRate() {
        return vacationRate;
    }

    public BigDecimal getSickRate() {
        return sickRate;
    }

    @Override
    public String getViewType() {
        return "accruals";
    }
}
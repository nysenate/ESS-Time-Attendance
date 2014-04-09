package gov.nysenate.seta.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * The accrual balances for a specific employee and pay period.
 */
public class AccrualInfo
{
    protected int employeeId;
    protected PayPeriod payPeriod;

    /** Hours accrued (gained) */
    protected BigDecimal personalAccrued = BigDecimal.ZERO;
    protected BigDecimal sickAccrued = BigDecimal.ZERO;
    protected BigDecimal vacationAccrued = BigDecimal.ZERO;

    /** Hours used (lost) */
    protected BigDecimal personalUsed = BigDecimal.ZERO;
    protected BigDecimal sickEmpUsed = BigDecimal.ZERO;
    protected BigDecimal sickFamUsed = BigDecimal.ZERO;
    protected BigDecimal vacationUsed = BigDecimal.ZERO;
    protected BigDecimal miscUsed = BigDecimal.ZERO;

    /** Accrual rates for the pay period */
    protected BigDecimal vacationRate = BigDecimal.ZERO;
    protected BigDecimal sickRate = BigDecimal.ZERO;

    /** Year to date work totals */
    protected BigDecimal ytdExpected = BigDecimal.ZERO;
    protected BigDecimal ytdActual = BigDecimal.ZERO;

    public AccrualInfo() {}

    /** Basic Getters/Setters */

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getPersonalAccrued() {
        return personalAccrued;
    }

    public void setPersonalAccrued(BigDecimal personalAccrued) {
        this.personalAccrued = personalAccrued;
    }

    public BigDecimal getSickAccrued() {
        return sickAccrued;
    }

    public void setSickAccrued(BigDecimal sickAccrued) {
        this.sickAccrued = sickAccrued;
    }

    public BigDecimal getVacationAccrued() {
        return vacationAccrued;
    }

    public void setVacationAccrued(BigDecimal vacationAccrued) {
        this.vacationAccrued = vacationAccrued;
    }

    public BigDecimal getPersonalUsed() {
        return personalUsed;
    }

    public void setPersonalUsed(BigDecimal personalUsed) {
        this.personalUsed = personalUsed;
    }

    public BigDecimal getSickEmpUsed() {
        return sickEmpUsed;
    }

    public void setSickEmpUsed(BigDecimal sickEmpUsed) {
        this.sickEmpUsed = sickEmpUsed;
    }

    public BigDecimal getSickFamUsed() {
        return sickFamUsed;
    }

    public void setSickFamUsed(BigDecimal sickFamUsed) {
        this.sickFamUsed = sickFamUsed;
    }

    public BigDecimal getVacationUsed() {
        return vacationUsed;
    }

    public void setVacationUsed(BigDecimal vacationUsed) {
        this.vacationUsed = vacationUsed;
    }

    public BigDecimal getMiscUsed() {
        return miscUsed;
    }

    public void setMiscUsed(BigDecimal miscUsed) {
        this.miscUsed = miscUsed;
    }

    public BigDecimal getVacationRate() {
        return vacationRate;
    }

    public void setVacationRate(BigDecimal vacationRate) {
        this.vacationRate = vacationRate;
    }

    public BigDecimal getSickRate() {
        return sickRate;
    }

    public void setSickRate(BigDecimal sickRate) {
        this.sickRate = sickRate;
    }

    public BigDecimal getYtdExpected() {
        return ytdExpected;
    }

    public void setYtdExpected(BigDecimal ytdExpected) {
        this.ytdExpected = ytdExpected;
    }

    public BigDecimal getYtdActual() {
        return ytdActual;
    }

    public void setYtdActual(BigDecimal ytdActual) {
        this.ytdActual = ytdActual;
    }
}

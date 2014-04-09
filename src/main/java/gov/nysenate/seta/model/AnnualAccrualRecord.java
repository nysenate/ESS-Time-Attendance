package gov.nysenate.seta.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Helper class to represent annual accrual summaries. This model is intended to be used
 * within the dao layer. The AccuralInfo model should be used to represent end-user accrual
 * summaries.
 *
 * @see gov.nysenate.seta.model.AccrualInfo
 */
public class AnnualAccrualRecord
{
    int year;
    Date closeDate;
    Date endDate;
    BigDecimal workHoursTotal;
    BigDecimal travelHoursTotal;
    BigDecimal vacHoursUsed;
    BigDecimal vacHoursAccrued;
    BigDecimal vacHoursBanked;
    BigDecimal perHoursUsed;
    BigDecimal perHoursAccrued;
    BigDecimal empHoursUsed;
    BigDecimal famHoursUsed;
    BigDecimal empHoursAccrued;
    BigDecimal empHoursBanked;
    BigDecimal holHoursUsed;
    BigDecimal miscHoursUsed;
    int payPeriodsYtd;
    int payPeriodsBanked;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getWorkHoursTotal() {
        return workHoursTotal;
    }

    public void setWorkHoursTotal(BigDecimal workHoursTotal) {
        this.workHoursTotal = workHoursTotal;
    }

    public BigDecimal getTravelHoursTotal() {
        return travelHoursTotal;
    }

    public void setTravelHoursTotal(BigDecimal travelHoursTotal) {
        this.travelHoursTotal = travelHoursTotal;
    }

    public BigDecimal getVacHoursUsed() {
        return vacHoursUsed;
    }

    public void setVacHoursUsed(BigDecimal vacHoursUsed) {
        this.vacHoursUsed = vacHoursUsed;
    }

    public BigDecimal getVacHoursAccrued() {
        return vacHoursAccrued;
    }

    public void setVacHoursAccrued(BigDecimal vacHoursAccrued) {
        this.vacHoursAccrued = vacHoursAccrued;
    }

    public BigDecimal getVacHoursBanked() {
        return vacHoursBanked;
    }

    public void setVacHoursBanked(BigDecimal vacHoursBanked) {
        this.vacHoursBanked = vacHoursBanked;
    }

    public BigDecimal getPerHoursUsed() {
        return perHoursUsed;
    }

    public void setPerHoursUsed(BigDecimal perHoursUsed) {
        this.perHoursUsed = perHoursUsed;
    }

    public BigDecimal getPerHoursAccrued() {
        return perHoursAccrued;
    }

    public void setPerHoursAccrued(BigDecimal perHoursAccrued) {
        this.perHoursAccrued = perHoursAccrued;
    }

    public BigDecimal getEmpHoursUsed() {
        return empHoursUsed;
    }

    public void setEmpHoursUsed(BigDecimal empHoursUsed) {
        this.empHoursUsed = empHoursUsed;
    }

    public BigDecimal getFamHoursUsed() {
        return famHoursUsed;
    }

    public void setFamHoursUsed(BigDecimal famHoursUsed) {
        this.famHoursUsed = famHoursUsed;
    }

    public BigDecimal getEmpHoursAccrued() {
        return empHoursAccrued;
    }

    public void setEmpHoursAccrued(BigDecimal empHoursAccrued) {
        this.empHoursAccrued = empHoursAccrued;
    }

    public BigDecimal getEmpHoursBanked() {
        return empHoursBanked;
    }

    public void setEmpHoursBanked(BigDecimal empHoursBanked) {
        this.empHoursBanked = empHoursBanked;
    }

    public BigDecimal getHolHoursUsed() {
        return holHoursUsed;
    }

    public void setHolHoursUsed(BigDecimal holHoursUsed) {
        this.holHoursUsed = holHoursUsed;
    }

    public BigDecimal getMiscHoursUsed() {
        return miscHoursUsed;
    }

    public void setMiscHoursUsed(BigDecimal miscHoursUsed) {
        this.miscHoursUsed = miscHoursUsed;
    }

    public int getPayPeriodsYtd() {
        return payPeriodsYtd;
    }

    public void setPayPeriodsYtd(int payPeriodsYtd) {
        this.payPeriodsYtd = payPeriodsYtd;
    }

    public int getPayPeriodsBanked() {
        return payPeriodsBanked;
    }

    public void setPayPeriodsBanked(int payPeriodsBanked) {
        this.payPeriodsBanked = payPeriodsBanked;
    }
}

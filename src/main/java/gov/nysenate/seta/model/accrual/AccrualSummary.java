package gov.nysenate.seta.model.accrual;

import java.math.BigDecimal;

/**
 * Holds basic accrual information (hours accrued/used/banked). This class is intended to
 * be sub classed with additional context such as the date bounds (e.g. pay period or year).
 */
public class AccrualSummary extends AccrualUsage
{
    BigDecimal vacHoursAccrued;
    BigDecimal vacHoursBanked;
    BigDecimal perHoursAccrued;
    BigDecimal empHoursAccrued;
    BigDecimal empHoursBanked;

    public AccrualSummary() {}

    /** --- Functional Getters/Setters --- */

    public BigDecimal getVacHoursRemaining() {
        return vacHoursBanked.add(vacHoursAccrued).subtract(vacHoursUsed);
    }

    public BigDecimal getPerHoursRemaining() {
        return perHoursAccrued.subtract(perHoursUsed);
    }

    public BigDecimal getEmpHoursRemaining() {
        return empHoursBanked.add(empHoursAccrued).subtract(empHoursUsed).subtract(famHoursUsed);
    }

    /** --- Basic Getters/Setters --- */

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

    public BigDecimal getPerHoursAccrued() {
        return perHoursAccrued;
    }

    public void setPerHoursAccrued(BigDecimal perHoursAccrued) {
        this.perHoursAccrued = perHoursAccrued;
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
}

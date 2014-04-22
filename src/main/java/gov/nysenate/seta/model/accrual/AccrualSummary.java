package gov.nysenate.seta.model.accrual;

import java.math.BigDecimal;

/**
 * Holds basic accrual information (hours accrued/used/banked). This class is intended to
 * be sub classed with additional context such as the date bounds (e.g. pay period or year).
 */
public class AccrualSummary extends AccrualUsage
{
    protected static final BigDecimal MAX_VAC_ACCRUED = new BigDecimal(210);
    protected static final BigDecimal MAX_EMP_ACCRUED = new BigDecimal(1400);

    BigDecimal vacHoursAccrued;
    BigDecimal vacHoursBanked;
    BigDecimal perHoursAccrued;
    BigDecimal empHoursAccrued;
    BigDecimal empHoursBanked;

    public AccrualSummary() {}

    /** --- Functional Getters/Setters --- */

    public BigDecimal getVacHoursRemaining() {
        return getTotalVacHoursAccrued().subtract(vacHoursUsed);
    }

    public BigDecimal getPerHoursRemaining() {
        return perHoursAccrued.subtract(perHoursUsed);
    }

    public BigDecimal getEmpHoursRemaining() {
        return getTotalEmpHoursAccrued().subtract(empHoursUsed).subtract(famHoursUsed);
    }

    public BigDecimal getTotalVacHoursAccrued() {
        BigDecimal hrs = vacHoursBanked.add(vacHoursAccrued);
        return (hrs.compareTo(MAX_VAC_ACCRUED) <= 0) ? hrs : MAX_VAC_ACCRUED;
    }

    public BigDecimal getTotalEmpHoursAccrued() {
        BigDecimal hrs = empHoursBanked.add(empHoursAccrued);
        return (hrs.compareTo(MAX_EMP_ACCRUED) <= 0) ? hrs : MAX_EMP_ACCRUED;
    }

    /** --- Copy Constructor --- */

    public AccrualSummary(AccrualSummary s) {
        super(s);
        this.setEmpHoursAccrued(s.getEmpHoursAccrued());
        this.setEmpHoursBanked(s.getEmpHoursBanked());
        this.setPerHoursAccrued(s.getPerHoursAccrued());
        this.setVacHoursAccrued(s.getVacHoursAccrued());
        this.setVacHoursBanked(s.getVacHoursBanked());
        this.setEmpHoursUsed(s.getEmpHoursUsed());
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

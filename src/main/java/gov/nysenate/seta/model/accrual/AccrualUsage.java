package gov.nysenate.seta.model.accrual;

import java.math.BigDecimal;

/**
 * Helper class to store accrual usage numbers.
 */
public class AccrualUsage
{
    int empId;
    BigDecimal workHours;
    BigDecimal travelHoursUsed;
    BigDecimal vacHoursUsed;
    BigDecimal perHoursUsed;
    BigDecimal empHoursUsed;
    BigDecimal famHoursUsed;
    BigDecimal holHoursUsed;
    BigDecimal miscHoursUsed;

    public AccrualUsage() {}

    /** --- Functional Getters/Setters --- */

    /** The total hours is the sum of the hours used */
    public BigDecimal getTotalHours() {
        return workHours.add(travelHoursUsed).add(vacHoursUsed).add(perHoursUsed).add(empHoursUsed)
                        .add(famHoursUsed).add(holHoursUsed).add(miscHoursUsed);
    }

    /** --- Basic Getters/Setters --- */

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public BigDecimal getTravelHoursUsed() {
        return travelHoursUsed;
    }

    public void setTravelHoursUsed(BigDecimal travelHoursUsed) {
        this.travelHoursUsed = travelHoursUsed;
    }

    public BigDecimal getVacHoursUsed() {
        return vacHoursUsed;
    }

    public void setVacHoursUsed(BigDecimal vacHoursUsed) {
        this.vacHoursUsed = vacHoursUsed;
    }

    public BigDecimal getPerHoursUsed() {
        return perHoursUsed;
    }

    public void setPerHoursUsed(BigDecimal perHoursUsed) {
        this.perHoursUsed = perHoursUsed;
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
}

package gov.nysenate.seta.model.accrual;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Helper class to store accrual usage numbers.
 */
public class AccrualUsage
{
    int empId;
    BigDecimal workHours = new BigDecimal(0);
    BigDecimal travelHoursUsed = new BigDecimal(0);
    BigDecimal vacHoursUsed = new BigDecimal(0);
    BigDecimal perHoursUsed = new BigDecimal(0);
    BigDecimal empHoursUsed = new BigDecimal(0);
    BigDecimal famHoursUsed = new BigDecimal(0);
    BigDecimal holHoursUsed = new BigDecimal(0);
    BigDecimal miscHoursUsed = new BigDecimal(0);

    public AccrualUsage() {}

    public AccrualUsage(int empId) {
        this.empId = empId;
    }

    public AccrualUsage(AccrualUsage lhs) {
        this.empId = lhs.empId;
        this.workHours = lhs.workHours;
        this.travelHoursUsed = lhs.travelHoursUsed;
        this.vacHoursUsed = lhs.vacHoursUsed;
        this.perHoursUsed = lhs.perHoursUsed;
        this.empHoursUsed = lhs.empHoursUsed;
        this.famHoursUsed = lhs.famHoursUsed;
        this.holHoursUsed = lhs.holHoursUsed;
        this.miscHoursUsed = lhs.miscHoursUsed;
    }

    public AccrualUsage(int empId, Collection<AccrualUsage> usages) {
        this(usages.stream().reduce(new AccrualUsage(empId), AccrualUsage::add));
    }

    /** --- Public Methods --- */

    /**
     * Adds the hours of one accrual usage to another.  The usages must be for the same employee
     * @param rhs AccrualUsage
     * @param lhs AccrualUsage
     * @return AccrualUsage
     */
    public static AccrualUsage add(AccrualUsage lhs, AccrualUsage rhs) {
        if (lhs.empId != rhs.empId) {
            throw new IllegalArgumentException("You cannot add accrual usages from two different employees");
        }
        AccrualUsage result = new AccrualUsage(lhs);
        result.workHours = result.workHours.add(rhs.workHours);
        result.travelHoursUsed = result.travelHoursUsed.add(rhs.travelHoursUsed);
        result.vacHoursUsed = result.vacHoursUsed.add(rhs.vacHoursUsed);
        result.perHoursUsed = result.perHoursUsed.add(rhs.perHoursUsed);
        result.empHoursUsed = result.empHoursUsed.add(rhs.empHoursUsed);
        result.famHoursUsed = result.famHoursUsed.add(rhs.famHoursUsed);
        result.holHoursUsed = result.holHoursUsed.add(rhs.holHoursUsed);
        result.miscHoursUsed = result.miscHoursUsed.add(rhs.miscHoursUsed);
        return result;
    }

    /** --- Functional Getters/Setters --- */

    /** The total hours is the sum of the hours used */
    public BigDecimal getTotalHoursUsed() {
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

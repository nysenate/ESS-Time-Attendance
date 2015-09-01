package gov.nysenate.seta.model.personnel;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;

import java.time.LocalDate;

/**
 * Associates a supervisor to an employee during a specific time period.
 */
public class EmployeeSupInfo
{
    protected int empId;
    protected int supId;
    protected String empLastName;
    // The requested supervisor date range when this instance was created
    protected LocalDate startDate;
    protected LocalDate endDate;
    // The date range when this person was under the specified supervisor
    protected LocalDate supStartDate;
    protected LocalDate supEndDate;

    /** --- Constructors --- */

    public EmployeeSupInfo() {}

    public EmployeeSupInfo(int empId, int supId, LocalDate startDate, LocalDate endDate) {
        this.empId = empId;
        this.supId = supId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** --- Functional Getters/Setters --- */

    public Range<LocalDate> getEffectiveDateRange() {
        if (supEndDate == null) {
            return Range.atLeast(startDate);
        }
        return Range.closed(startDate, endDate);
    }

    /** --- Basic Getters/Setters --- */

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getSupId() {
        return supId;
    }

    public void setSupId(int supId) {
        this.supId = supId;
    }

    public String getEmpLastName() {
        return empLastName;
    }

    public void setEmpLastName(String empLastName) {
        this.empLastName = empLastName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getSupStartDate() {
        return supStartDate;
    }

    public void setSupStartDate(LocalDate supStartDate) {
        this.supStartDate = supStartDate;
    }

    public LocalDate getSupEndDate() {
        return supEndDate;
    }

    public void setSupEndDate(LocalDate supEndDate) {
        this.supEndDate = supEndDate;
    }
}

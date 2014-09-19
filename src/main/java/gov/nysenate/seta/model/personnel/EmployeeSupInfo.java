package gov.nysenate.seta.model.personnel;

import java.time.LocalDate;
import java.util.Date;

/**
 * Associates a supervisor to an employee during a specific time period.
 */
public class EmployeeSupInfo
{
    protected int empId;
    protected int supId;
    protected String empLastName;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected LocalDate supStartDate;
    protected LocalDate supEndDate;

    /** --- Constructors --- */

    public EmployeeSupInfo() {}

    public EmployeeSupInfo(int empId, LocalDate startDate, LocalDate endDate) {
        this.empId = empId;
        this.startDate = startDate;
        this.endDate = endDate;
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

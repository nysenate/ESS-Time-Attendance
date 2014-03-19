package gov.nysenate.seta.model;

import java.util.Date;

/**
 * Associates a supervisor to an employee during a specific time period.
 */
public class EmployeeSupInfo
{
    protected int empId;
    protected int supId;
    protected String empLastName;
    protected Date startDate;
    protected Date endDate;
    protected Date supStartDate;
    protected Date supEndDate;

    public EmployeeSupInfo() {}

    public EmployeeSupInfo(int empId, Date startDate, Date endDate) {
        this.empId = empId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getSupStartDate() {
        return supStartDate;
    }

    public void setSupStartDate(Date supStartDate) {
        this.supStartDate = supStartDate;
    }

    public Date getSupEndDate() {
        return supEndDate;
    }

    public void setSupEndDate(Date supEndDate) {
        this.supEndDate = supEndDate;
    }
}

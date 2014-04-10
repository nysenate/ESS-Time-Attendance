package gov.nysenate.seta.model.payroll;

import gov.nysenate.seta.model.personnel.Employee;

import java.math.BigDecimal;
import java.util.Date;

public class EmployeePayInfo
{
    protected Employee employee;
    protected PayType payType;
    protected BigDecimal biweeklySalary;
    protected Date payUpdatedDate;
    protected Date w4SignedDate;
    protected boolean directDepositActive;

    public EmployeePayInfo(Employee employee) {
        this.employee = employee;
    }

    public BigDecimal getBiweeklySalary() {
        return biweeklySalary;
    }

    public void setBiweeklySalary(BigDecimal biweeklySalary) {
        this.biweeklySalary = biweeklySalary;
    }

    public Date getPayUpdatedDate() {
        return payUpdatedDate;
    }

    public void setPayUpdatedDate(Date payUpdatedDate) {
        this.payUpdatedDate = payUpdatedDate;
    }

    public Date getW4SignedDate() {
        return w4SignedDate;
    }

    public void setW4SignedDate(Date w4SignedDate) {
        this.w4SignedDate = w4SignedDate;
    }

    public boolean isDirectDepositActive() {
        return directDepositActive;
    }

    public void setDirectDepositActive(boolean directDepositActive) {
        this.directDepositActive = directDepositActive;
    }
}
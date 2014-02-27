package gov.nysenate.seta.model;

import java.math.BigDecimal;
import java.util.Date;

public class EmployeeFinancialInfo
{
    protected BigDecimal biweeklySalary;
    protected Date payUpdatedDate;
    protected Date w4SignedDate;
    protected boolean directDepositActive;

    public EmployeeFinancialInfo() {}

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
package gov.nysenate.seta.model.accrual;

import java.util.Date;

/**
 * Represents a summary of accruals over a given year.
 */
public class AnnualAccrualSummary extends AccrualSummary
{
    int year;
    Date endDate;
    Date closeDate;
    int payPeriodsYtd;
    int payPeriodsBanked;

    public AnnualAccrualSummary() {}

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
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
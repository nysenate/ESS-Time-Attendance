package gov.nysenate.seta.model.accrual;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Helper class to store accrual usage sums for a given year.
 */
public class AnnualAccrualUsage
{
    protected int year;
    protected Date latestStartDate;
    protected Date latestEndDate;
    protected BigDecimal workHours;
    protected BigDecimal personalUsed;
    protected BigDecimal sickEmpUsed;
    protected BigDecimal sickFamUsed;
    protected BigDecimal vacationUsed;
    protected BigDecimal miscUsed;
    protected BigDecimal totalHours;

    public AnnualAccrualUsage() {}

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getLatestStartDate() {
        return latestStartDate;
    }

    public void setLatestStartDate(Date latestStartDate) {
        this.latestStartDate = latestStartDate;
    }

    public Date getLatestEndDate() {
        return latestEndDate;
    }

    public void setLatestEndDate(Date latestEndDate) {
        this.latestEndDate = latestEndDate;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public BigDecimal getPersonalUsed() {
        return personalUsed;
    }

    public void setPersonalUsed(BigDecimal personalUsed) {
        this.personalUsed = personalUsed;
    }

    public BigDecimal getSickEmpUsed() {
        return sickEmpUsed;
    }

    public void setSickEmpUsed(BigDecimal sickEmpUsed) {
        this.sickEmpUsed = sickEmpUsed;
    }

    public BigDecimal getSickFamUsed() {
        return sickFamUsed;
    }

    public void setSickFamUsed(BigDecimal sickFamUsed) {
        this.sickFamUsed = sickFamUsed;
    }

    public BigDecimal getVacationUsed() {
        return vacationUsed;
    }

    public void setVacationUsed(BigDecimal vacationUsed) {
        this.vacationUsed = vacationUsed;
    }

    public BigDecimal getMiscUsed() {
        return miscUsed;
    }

    public void setMiscUsed(BigDecimal miscUsed) {
        this.miscUsed = miscUsed;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }
}

package gov.nysenate.seta.model.allowances;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by heitner on 6/26/2014.
 */
public class AllowanceUsage {
    int empId;
    int year;
    BigDecimal moneyUsed;
    BigDecimal moneyAllowed;
    BigDecimal hoursUsed;
    BigDecimal hoursAllowed;
    Date endDate;
    List<HashMap<String, BigDecimal>> hourlyRate;

    public AllowanceUsage() {

    }

    /** --- Copy Constructor --- */

    public AllowanceUsage(AllowanceUsage a) {
        this.setEmpId(a.getEmpId());
        this.setYear(a.getYear());
        this.setHoursAllowed(a.getHoursAllowed());
        this.setHoursUsed(a.getHoursUsed());
        this.setMoneyAllowed(a.getMoneyAllowed());
        this.setMoneyUsed(a.getMoneyUsed());
    }

    /** --- Basic Getters/Setters --- */

    public int getEmpId() { return empId; }

    public void setEmpId(int empId) { this.empId = empId; }

    public int getYear() { return year; }

    public void setYear(int year) { this.year = year; }

    public BigDecimal getMoneyUsed() {
        return moneyUsed;
    }

    public void setMoneyUsed(BigDecimal moneyUsed) {
        this.moneyUsed = moneyUsed;
    }

    public BigDecimal getMoneyAllowed() {
        return moneyAllowed;
    }

    public void setMoneyAllowed(BigDecimal moneyAllowed) {
        this.moneyAllowed = moneyAllowed;
    }

    public BigDecimal getHoursUsed() {
        return hoursUsed;
    }

    public void setHoursUsed(BigDecimal hoursUsed) {
        this.hoursUsed = hoursUsed;
    }

    public BigDecimal getHoursAllowed() {
        return hoursAllowed;
    }

    public void setHoursAllowed(BigDecimal hoursAllowed) {
        this.hoursAllowed = hoursAllowed;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}

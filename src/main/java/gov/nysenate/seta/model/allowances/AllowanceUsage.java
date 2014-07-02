package gov.nysenate.seta.model.allowances;

import gov.nysenate.seta.model.payroll.SalaryRec;
import gov.nysenate.seta.model.payroll.SalaryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by heitner on 6/26/2014.
 */
public class AllowanceUsage {
    int empId;
    int year;
    BigDecimal moneyUsed;
    BigDecimal moneyAllowed;
    BigDecimal moneyAvailable;
    BigDecimal hoursUsed;
    BigDecimal hoursAvailable;
    Date endDate;
    List<SalaryRec> salaryRecs;
    SalaryType salaryType;
    private static final Logger logger = LoggerFactory.getLogger(AllowanceUsage.class);


    public AllowanceUsage() {

    }

    /** --- Copy Constructor --- */

    public AllowanceUsage(AllowanceUsage a) {
        this.setEmpId(a.getEmpId());
        this.setYear(a.getYear());
        this.setHoursAvailable(a.getHoursAvailable());
        this.setHoursUsed(a.getHoursUsed());
        this.setMoneyAllowed(a.getMoneyAllowed());
        this.setMoneyUsed(a.getMoneyUsed());
        this.setSalaryType(a.getSalaryType());
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
        computeAvailableMoney();
            }

    public BigDecimal getMoneyAllowed() {
        return moneyAllowed;
    }

    public void setMoneyAllowed(BigDecimal moneyAllowed) {
        this.moneyAllowed = moneyAllowed;
        computeAvailableMoney();
    }

    public BigDecimal getHoursUsed() {
        return hoursUsed;
    }

    public void setHoursUsed(BigDecimal hoursUsed) {
        this.hoursUsed = hoursUsed;
        computeAvailableMoney();
    }

    public BigDecimal getHoursAvailable() {
        return hoursAvailable;
    }

    public void setHoursAvailable(BigDecimal hoursAllowed) {
        this.hoursAvailable = hoursAvailable;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public SalaryType getSalaryType() { return salaryType; }

    public void setSalaryType(SalaryType salaryType) {
        this.salaryType = salaryType;
    }

    public void setSalaryRecs(List<SalaryRec> salaryRecs) {
        this.salaryRecs = salaryRecs;
        computeAvailableMoney();
    }

     public List<SalaryRec> getSalaryRecs () {
         return salaryRecs;
     }

    private void computeAvailableMoney() {
        logger.debug("computeAvailableMoney");
        if (salaryRecs != null && salaryRecs.size() > 0 && moneyUsed != null && moneyUsed.floatValue() >= 0f && moneyAllowed != null && moneyAllowed.floatValue() > 0) {
            logger.debug("computeAvailableMoney can compute  values");
            float latestSalary = 0f;
            int recToUse = 0;
            if (salaryRecs.get(0).getEffectDate().before(salaryRecs.get(salaryRecs.size() - 1).getEffectDate())) {
                logger.debug("computeAvailableMoney last date should be the latest");
                recToUse = salaryRecs.size()-1;
            }
            latestSalary = salaryRecs.get(recToUse).getSalary().floatValue();
            logger.debug("computeAvailableMoney using salary("+recToUse+"):"+latestSalary);

            moneyAvailable = new BigDecimal(moneyAllowed.floatValue() - moneyUsed.floatValue());
            logger.debug("computeAvailableMoney Money Available:"+moneyAvailable.floatValue());
            if (latestSalary > 0f) {
                hoursAvailable = new BigDecimal(moneyAvailable.floatValue() / latestSalary);
                logger.debug("computeAvailableMoney Hours Available:"+hoursAvailable.floatValue());
            }
        }
    }

}

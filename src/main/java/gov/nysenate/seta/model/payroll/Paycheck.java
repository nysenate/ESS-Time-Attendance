package gov.nysenate.seta.model.payroll;

import gov.nysenate.seta.model.personnel.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paycheck
{
    private final BigDecimal payRate;
    private final String empFullName;
    private final String empJobTitle;
    private final String payPeriod;
    private final LocalDate checkDate;
    private final String agencyCode;
    private final String lineNum;
    private final BigDecimal grossIncome;
    private final BigDecimal netIncome;
    private List<Deduction> deductions;
    /** Amount payed via Direct Deposit. */
    private final BigDecimal directDepositAmount;
    /** Amount payed via check. */
    private final BigDecimal checkAmount;

    public Paycheck(BigDecimal payRate, String empFullName, String empJobTitle, String payPeriod, LocalDate checkDate,
                    String agencyCode, String lineNum, BigDecimal grossIncome, BigDecimal netIncome,
                    BigDecimal directDepositAmount, BigDecimal checkAmount) {
        this.payRate = payRate;
        this.empFullName = empFullName;
        this.empJobTitle = empJobTitle;
        this.payPeriod = payPeriod;
        this.checkDate = checkDate;
        this.agencyCode = agencyCode;
        this.lineNum = lineNum;
        this.grossIncome = grossIncome;
        this.netIncome = netIncome;
        this.directDepositAmount = directDepositAmount;
        this.checkAmount = checkAmount;
        this.deductions = new ArrayList<>();
    }

    /** Functional Methods */

    public void addDeduction(Deduction d) {
        this.deductions.add(d);
    }

    public BigDecimal getTotalDeductions() {
        return deductions.stream().map(Deduction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Basic Getters */

    public BigDecimal getPayRate() {
        return payRate;
    }

    public String getEmpFullName() {
        return empFullName;
    }

    public String getEmpJobTitle() {
        return empJobTitle;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public String getLineNum() {
        return lineNum;
    }

    public BigDecimal getGrossIncome() {
        return grossIncome;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public List<Deduction> getDeductions() {
        return deductions;
    }

    public BigDecimal getDirectDepositAmount() {
        return directDepositAmount;
    }

    public BigDecimal getCheckAmount() {
        return checkAmount;
    }

}

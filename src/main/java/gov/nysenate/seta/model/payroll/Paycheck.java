package gov.nysenate.seta.model.payroll;

import gov.nysenate.seta.model.personnel.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Paycheck
{
    private final Employee employee;
    private final String payPeriod;
    private final LocalDate checkDate;
    private final String agencyCode;
    private final String lineNum;
    private final BigDecimal grossIncome;
    private final BigDecimal netIncome;
    private final List<Deduction> deductions;
    /** Amount payed via Direct Deposit. */
    private final BigDecimal directDepositAmount;
    /** Amount payed via check. */
    private final BigDecimal checkAmount;

    public Paycheck(Employee employee, String payPeriod, LocalDate checkDate, String agencyCode, String lineNum, BigDecimal grossIncome,
                    BigDecimal netIncome, List<Deduction> deductions, BigDecimal directDepositAmount, BigDecimal checkAmount) {
        this.employee = employee;
        this.payPeriod = payPeriod;
        this.checkDate = checkDate;
        this.agencyCode = agencyCode;
        this.lineNum = lineNum;
        this.grossIncome = grossIncome;
        this.netIncome = netIncome;
        this.deductions = deductions;
        this.directDepositAmount = directDepositAmount;
        this.checkAmount = checkAmount;
    }

    /** Functional Methods */

    public BigDecimal getTotalDeductions() {
        return deductions.stream().map(Deduction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Basic Getters */

    public Employee getEmployee() {
        return employee;
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

package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.payroll.Deduction;
import gov.nysenate.seta.model.payroll.Paycheck;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@XmlRootElement
public class PaycheckView implements ViewObject
{
    protected String empFullName;
    protected String empJobTitle;
    protected BigDecimal payRate;
    protected String payPeriod;
    protected LocalDate checkDate;
    protected String agencyCode;
    protected String lineNum;
    protected BigDecimal grossIncome;
    protected BigDecimal netIncome;
    /** Map of deduction code to deduction for a paycheck. Makes for less work displaying in client. */
    protected TreeMap<String, DeductionView> deductions;
    protected BigDecimal directDepositAmount;
    protected BigDecimal checkAmount;

    public PaycheckView(Paycheck paycheck) {
        this.empFullName = paycheck.getEmpFullName();
        this.empJobTitle = paycheck.getEmpJobTitle();
        this.payRate = paycheck.getPayRate();
        this.payPeriod = paycheck.getPayPeriod();
        this.checkDate = paycheck.getCheckDate();
        this.agencyCode = paycheck.getAgencyCode();
        this.lineNum = paycheck.getLineNum();
        this.grossIncome = paycheck.getGrossIncome();
        this.netIncome = paycheck.getNetIncome();
        this.deductions = paycheck.getDeductions().stream().collect(toMap(Deduction::getDescription, DeductionView::new, (a,b) -> a, TreeMap::new));
        this.directDepositAmount = paycheck.getDirectDepositAmount();
        this.checkAmount = paycheck.getCheckAmount();
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "Paycheck";
    }

    @XmlElement
    public String getEmpFullName() {
        return empFullName;
    }

    @XmlElement
    public String getEmpJobTitle() {
        return empJobTitle;
    }

    @XmlElement
    public BigDecimal getPayRate() {
        return payRate;
    }

    @XmlElement
    public String getPayPeriod() {
        return payPeriod;
    }

    @XmlElement
    public LocalDate getCheckDate() {
        return checkDate;
    }

    @XmlElement
    public String getAgencyCode() {
        return agencyCode;
    }

    @XmlElement
    public String getLineNum() {
        return lineNum;
    }

    @XmlElement
    public BigDecimal getGrossIncome() {
        return grossIncome;
    }

    @XmlElement
    public BigDecimal getNetIncome() {
        return netIncome;
    }

    @XmlElement
    public TreeMap<String, DeductionView> getDeductions() {
        return deductions;
    }

    @XmlElement
    public BigDecimal getDirectDepositAmount() {
        return directDepositAmount;
    }

    @XmlElement
    public BigDecimal getCheckAmount() {
        return checkAmount;
    }
}

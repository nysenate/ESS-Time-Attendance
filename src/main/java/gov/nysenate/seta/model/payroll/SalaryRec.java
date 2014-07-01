package gov.nysenate.seta.model.payroll;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Heitner on 6/30/2014.
 */

public class SalaryRec {
    private BigDecimal mosalbiwkly;
    private Date dteffect;
    private Date dteffectEnd;
    private SalaryType salaryType;
    public SalaryRec() {}

    public BigDecimal getSalary() {
        return mosalbiwkly;
    }
    public void setSalary(BigDecimal mosalbiwkly) {
        this.mosalbiwkly = mosalbiwkly;
    }
    public Date getEffectDate() {
        return dteffect;
    }
    public void setEffectDate(Date dteffect) {
        this.dteffect = dteffect;
    }
    public Date getEffectEndDate() {
        return dteffectEnd;
    }

    public void setEffectEndDate(Date dteffectEnd) {
        this.dteffectEnd = dteffectEnd;
    }

    public SalaryType getSalaryType(SalaryType salaryType) {
        return salaryType;
    }

    public void setSalaryType(SalaryType salaryType) {
        this.salaryType =  salaryType;
    }

}

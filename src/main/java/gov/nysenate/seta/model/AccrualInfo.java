package gov.nysenate.seta.model;

import java.util.Map;

public class AccrualInfo
{
    protected PayPeriod payPeriod;
    protected Employee employee;
    protected Map<AccrualType, Integer> accruedHours;
    protected Map<AccrualType, Integer> usedHours;
    protected Map<AccrualType, Integer> accrualRate;

    public AccrualInfo() {}

    /** Basic Getters/Setters */

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Map<AccrualType, Integer> getAccruedHours() {
        return accruedHours;
    }

    public void setAccruedHours(Map<AccrualType, Integer> accruedHours) {
        this.accruedHours = accruedHours;
    }

    public Map<AccrualType, Integer> getUsedHours() {
        return usedHours;
    }

    public void setUsedHours(Map<AccrualType, Integer> usedHours) {
        this.usedHours = usedHours;
    }

    public Map<AccrualType, Integer> getAccrualRate() {
        return accrualRate;
    }

    public void setAccrualRate(Map<AccrualType, Integer> accrualRate) {
        this.accrualRate = accrualRate;
    }
}

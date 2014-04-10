package gov.nysenate.seta.model.accrual;

import gov.nysenate.seta.model.period.PayPeriod;

import java.util.LinkedHashMap;

public class AccrualHistory
{
    protected int employeeId;
    protected LinkedHashMap<PayPeriod, AccrualInfo> accrualInfoMap;

    public AccrualHistory() {
        this.accrualInfoMap = new LinkedHashMap<>();
    }
}

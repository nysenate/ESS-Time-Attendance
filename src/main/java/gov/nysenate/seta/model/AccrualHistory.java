package gov.nysenate.seta.model;

import java.util.Date;
import java.util.LinkedHashMap;

public class AccrualHistory
{
    protected int employeeId;
    protected LinkedHashMap<Date, AccrualInfo> accrualInfoMap;

    public AccrualHistory() {
        this.accrualInfoMap = new LinkedHashMap<>();
    }
}

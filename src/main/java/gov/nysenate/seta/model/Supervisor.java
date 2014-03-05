package gov.nysenate.seta.model;

import java.util.Date;
import java.util.List;

public class Supervisor extends Employee
{
    protected List<Integer> employeeIds;
    protected boolean hasOverrides;
    protected Date supEffectiveStart;
    protected Date supEffectiveEnd;

    public Supervisor() {}
}

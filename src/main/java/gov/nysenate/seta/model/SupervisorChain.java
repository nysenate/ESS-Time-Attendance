package gov.nysenate.seta.model;

import java.util.List;
import java.util.Map;

public class SupervisorChain
{
    protected int supervisorId;
    protected Map<Integer, Supervisor> supervisorMap;
    protected List<Integer> supChainList;

    public SupervisorChain() {}
}

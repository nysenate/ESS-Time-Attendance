package gov.nysenate.seta.model;

import java.util.List;
import java.util.Map;

public class SupervisorChain
{
    protected Supervisor supervisor;
    protected Map<Integer, Supervisor> supervisorMap;
    protected List<Integer> supChainList;

    public SupervisorChain(Supervisor supervisor) {
        this.supervisor = supervisor;
    }
}

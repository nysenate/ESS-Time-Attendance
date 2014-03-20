package gov.nysenate.seta.model;

import java.util.HashSet;
import java.util.LinkedList;

public class SupervisorChain
{
    protected int supervisorId;
    protected LinkedList<Integer> chainList = new LinkedList<>();
    private HashSet<Integer> supSet = new HashSet<>();

    public SupervisorChain() {}

    public SupervisorChain(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public void addSupervisorToChain(int nextSupId) {
        if (!supSet.contains(nextSupId)) {
            chainList.add(nextSupId);
            supSet.add(nextSupId);
        }
    }

    public boolean containsSupervisor(int supId) {
        return supSet.contains(supId);
    }

    public boolean isInOwnChain() {
        return supSet.contains(this.supervisorId);
    }

    /** Basic Getters/Setters */

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public LinkedList<Integer> getChainList() {
        return chainList;
    }
}

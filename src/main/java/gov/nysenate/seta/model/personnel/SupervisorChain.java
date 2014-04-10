package gov.nysenate.seta.model.personnel;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * An employee has a supervisor hierarchy that is represented as a chain. The chain
 * is useful for determining who can be granted a supervisor override if the employee is a
 * supervisor.
 */
/** TODO Add chain exceptions */
public class SupervisorChain
{
    protected int employeeId;
    protected LinkedList<Integer> chainList = new LinkedList<>();
    protected HashSet<Integer> chainInclusions = new HashSet<>();
    protected HashSet<Integer> chainExclusions = new HashSet<>();
    private HashSet<Integer> supSet = new HashSet<>();

    public SupervisorChain() {}

    public SupervisorChain(int empId) {
        this.employeeId = empId;
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
        return supSet.contains(this.employeeId);
    }

    /** Basic Getters/Setters */

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LinkedList<Integer> getChainList() {
        return chainList;
    }

    public HashSet<Integer> getChainInclusions() {
        return chainInclusions;
    }

    public HashSet<Integer> getChainExclusions() {
        return chainExclusions;
    }
}

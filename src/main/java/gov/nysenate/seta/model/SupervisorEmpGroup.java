package gov.nysenate.seta.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the employees that are managed by the associated supervisor.
 * The group of employees are for T&A purposes only and do not necessarily reflect
 * organizational hierarchy.
 */
public class SupervisorEmpGroup
{
    protected int supervisorId;

    /** List of direct employees */
    protected List<Integer> primaryEmpList;

    /** Map of employees obtained by supervisor overrides.
     *  (Supervisor id -> List of their employee ids) */
    protected Map<Integer, List<Integer>> supOverrideEmpMap;

    /** List of other employees that this supervisor also has access to */
    protected List<Integer> empOverrideList;

    public SupervisorEmpGroup() {}

    /** Basic Getters/Setters */

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public List<Integer> getPrimaryEmpList() {
        return primaryEmpList;
    }

    public void setPrimaryEmpList(List<Integer> primaryEmpList) {
        this.primaryEmpList = primaryEmpList;
    }

    public Map<Integer, List<Integer>> getSupOverrideEmpMap() {
        return supOverrideEmpMap;
    }

    public void setSupOverrideEmpMap(Map<Integer, List<Integer>> supOverrideEmpMap) {
        this.supOverrideEmpMap = supOverrideEmpMap;
    }

    public List<Integer> getEmpOverrideList() {
        return empOverrideList;
    }

    public void setEmpOverrideList(List<Integer> empOverrideList) {
        this.empOverrideList = empOverrideList;
    }
}

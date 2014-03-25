package gov.nysenate.seta.model;

import java.util.Date;
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
    protected Date startDate;
    protected Date endDate;

    /** Primary employees (directly assigned to this supervisor).
     *  Mapping of empId -> EmployeeSupInfo */
    protected Map<Integer, EmployeeSupInfo> primaryEmployees;

    /** Override employees are specific employees that this supervisor was given access to.
     *  Mapping of empId -> EmployeeSupInfo */
    protected Map<Integer, EmployeeSupInfo> overrideEmployees;

    /** Supervisor override employees are all the primary employees for the supervisors that
     *  granted override access.
     *  Mapping of the override granter supId -> (Map of empId -> EmployeeInfo) */
    protected Map<Integer, Map<Integer, EmployeeSupInfo>> supOverrideEmployees;

    public SupervisorEmpGroup() {}

    public SupervisorEmpGroup(int supervisorId, Date startDate, Date endDate) {
        this.supervisorId = supervisorId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * @return true if any employees are stored, false otherwise.
     */
    public boolean hasEmployees() {
        return (primaryEmployees != null && !primaryEmployees.isEmpty()) ||
               (overrideEmployees != null && !overrideEmployees.isEmpty()) ||
               (supOverrideEmployees != null && !supOverrideEmployees.isEmpty());
    }

    /** Basic Getters/Setters */

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Map<Integer, EmployeeSupInfo> getPrimaryEmployees() {
        return primaryEmployees;
    }

    public void setPrimaryEmployees(Map<Integer, EmployeeSupInfo> primaryEmployees) {
        this.primaryEmployees = primaryEmployees;
    }

    public Map<Integer, EmployeeSupInfo> getOverrideEmployees() {
        return overrideEmployees;
    }

    public void setOverrideEmployees(Map<Integer, EmployeeSupInfo> overrideEmployees) {
        this.overrideEmployees = overrideEmployees;
    }

    public Map<Integer, Map<Integer, EmployeeSupInfo>> getSupOverrideEmployees() {
        return supOverrideEmployees;
    }

    public void setSupOverrideEmployees(Map<Integer, Map<Integer, EmployeeSupInfo>> supOverrideEmployees) {
        this.supOverrideEmployees = supOverrideEmployees;
    }
}
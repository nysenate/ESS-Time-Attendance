package gov.nysenate.seta.model.personnel;

import com.google.common.collect.Table;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * Represents the employees that are managed by the certain supervisor.
 * Note: The group of employees are for T&A purposes only and do not necessarily reflect
 * organizational hierarchy.
 */
public class SupervisorEmpGroup
{
    /** The employee id of the supervisor this group is associated with. */
    protected int supervisorId;

    /** Employees were under this supervisor on/after this date. */
    protected LocalDate startDate;

    /** Employees were under this supervisor before/on this date. */
    protected LocalDate endDate;

    /** Primary employees that directly assigned to this supervisor.
     *  Mapping of empId -> EmployeeSupInfo */
    protected Map<Integer, EmployeeSupInfo> primaryEmployees;

    /** Override employees are specific employees that this supervisor was given access to.
     *  Mapping of empId -> EmployeeSupInfo */
    protected Map<Integer, EmployeeSupInfo> overrideEmployees;

    /** Supervisor override employees are all the primary employees for the supervisors that
     *  granted override access.
     *  Mapping of the (override granter supId, empId) -> EmployeeInfo */
    protected Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees;

    /** --- Constructors --- */

    public SupervisorEmpGroup() {}

    public SupervisorEmpGroup(int supervisorId, LocalDate startDate, LocalDate endDate) {
        this.supervisorId = supervisorId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** --- Methods --- */

    /**
     * @return true if any employees are stored, false otherwise.
     */
    public boolean hasEmployees() {
        return (primaryEmployees != null && !primaryEmployees.isEmpty()) ||
               (overrideEmployees != null && !overrideEmployees.isEmpty()) ||
               (supOverrideEmployees != null && !supOverrideEmployees.isEmpty());
    }

    /** --- Functional Getters/Setters --- */

    public Set<Integer> getOverrideSupIds() {
        return supOverrideEmployees.rowKeySet();
    }

    /**
     * Get overridden employees granted by the given supervisor id
     */
    public Map<Integer, EmployeeSupInfo> getSupOverrideEmployees(int supId) {
        return supOverrideEmployees.rowMap().get(supId);
    }

    /** --- Basic Getters/Setters --- */

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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

    public Table<Integer, Integer, EmployeeSupInfo> getSupOverrideEmployees() {
        return supOverrideEmployees;
    }

    public void setSupOverrideEmployees(Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees) {
        this.supOverrideEmployees = supOverrideEmployees;
    }
}
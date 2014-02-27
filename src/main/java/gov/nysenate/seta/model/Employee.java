package gov.nysenate.seta.model;

public class Employee extends Person
{
    protected int employeeId;
    protected int supervisorId;
    protected String uid;
    protected boolean active;
    protected ResponsibilityCenter respCenter;
    protected Location location;

    public Employee() {}

    /** Basic Getters/Setters */

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ResponsibilityCenter getRespCenter() {
        return respCenter;
    }

    public void setRespCenter(ResponsibilityCenter respCenter) {
        this.respCenter = respCenter;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
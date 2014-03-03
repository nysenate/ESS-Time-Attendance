package gov.nysenate.seta.model;

public class Employee extends Person
{
    protected int employeeId;
    protected int supervisorId;
    protected boolean active;
    protected String uid;
    protected String jobTitle;
    protected PayType payType;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
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
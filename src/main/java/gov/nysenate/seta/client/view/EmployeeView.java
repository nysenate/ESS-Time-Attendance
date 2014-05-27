package gov.nysenate.seta.client.view;

import gov.nysenate.seta.model.personnel.Employee;

public class EmployeeView
{
    protected int employeeId;
    protected String uid;

    protected String title;
    protected String firstName;
    protected String lastName;
    protected String initial;
    protected String suffix;
    protected String fullName;
    protected String email;

    public EmployeeView(Employee employee) {
        if (employee != null) {
            this.employeeId = employee.getEmployeeId();
            this.uid = employee.getUid();
            this.title = employee.getTitle();
            this.firstName = employee.getFirstName();
            this.lastName = employee.getLastName();
            this.initial = employee.getInitial();
            this.suffix = employee.getSuffix();
            this.fullName = employee.getFullName();
            this.email = employee.getEmail();
        }
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getInitial() {
        return initial;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}

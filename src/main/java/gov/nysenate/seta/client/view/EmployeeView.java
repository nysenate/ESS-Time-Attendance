package gov.nysenate.seta.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.Gender;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement
public class EmployeeView implements ViewObject
{
    protected int employeeId;
    protected String uid;
    protected String title;
    protected String firstName;
    protected String lastName;
    protected String initial;
    protected String suffix;
    protected String fullName;
    protected boolean active;
    protected String email;
    protected String workPhone;
    protected String homePhone;
    protected LocalDate dateOfBirth;
    protected String gender;

    public EmployeeView() {}

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
            this.active = employee.isActive();
            this.email = employee.getEmail();
            this.workPhone = employee.getWorkPhone();
            this.homePhone = employee.getHomePhone();
            this.dateOfBirth = employee.getDateOfBirth();
            this.gender = employee.getGender().name();
        }
    }

    @JsonIgnore
    public Employee toEmployee() {
        Employee emp = new Employee();
        emp.setEmployeeId(employeeId);
        emp.setUid(uid);
        emp.setTitle(title);
        emp.setFirstName(firstName);
        emp.setLastName(lastName);
        emp.setInitial(initial);
        emp.setSuffix(suffix);
        emp.setFullName(fullName);
        emp.setActive(active);
        emp.setEmail(email);
        emp.setWorkPhone(workPhone);
        emp.setHomePhone(homePhone);
        emp.setDateOfBirth(dateOfBirth);
        emp.setGender(Gender.valueOf(gender));
        return emp;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "employee";
    }

    @XmlElement
    public int getEmployeeId() {
        return employeeId;
    }

    @XmlElement
    public String getUid() {
        return uid;
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    @XmlElement
    public String getFirstName() {
        return firstName;
    }

    @XmlElement
    public String getLastName() {
        return lastName;
    }

    @XmlElement
    public String getInitial() {
        return initial;
    }

    @XmlElement
    public String getSuffix() {
        return suffix;
    }

    @XmlElement
    public String getFullName() {
        return fullName;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public String getEmail() {
        return email;
    }

    @XmlElement
    public String getWorkPhone() {
        return workPhone;
    }

    @XmlElement
    public String getHomePhone() {
        return homePhone;
    }

    @XmlElement
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @XmlElement
    public String getGender() {
        return gender;
    }
}
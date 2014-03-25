package gov.nysenate.seta.model;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Date;

/**
 * Represents basic information that is associated with a person.
 */
public class Person
{
    protected String title;
    protected String firstName;
    protected String lastName;
    protected String initial;
    protected String suffix;
    protected String fullName;
    protected String email;
    protected String workPhone;
    protected String homePhone;
    protected Date dateOfBirth;
    protected Gender gender;
    protected MaritalStatus maritalStatus;
    protected Address homeAddress;

    public Person() {}

    public Person(Person other) {
        this.title = other.title;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.initial = other.initial;
        this.suffix = other.suffix;
        this.fullName = other.fullName;
        this.email = other.email;
        this.workPhone = other.workPhone;
        this.homePhone = other.homePhone;
        this.dateOfBirth = other.dateOfBirth;
        this.gender = other.gender;
        this.maritalStatus = other.maritalStatus;
        this.homeAddress = other.homeAddress;
    }

    /** Functional Getters */

    /**
     * Returns the age of the person in years based on dateOfBirth.
     * @return int - age or null if dateOfBirth is null.
     */
    public int getAge() {
        if (dateOfBirth != null) {
            Period period = new Period(new DateTime(dateOfBirth), DateTime.now(), PeriodType.years());
            return period.getYears();
        }
        return -1;
    }

    /** Basic Getters/Setters */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
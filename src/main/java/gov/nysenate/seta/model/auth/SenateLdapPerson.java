package gov.nysenate.seta.model.auth;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

/**
 * Represents a person record in the Senate LDAP. This class is annotated such that methods
 * called by {@link org.springframework.ldap.core.LdapTemplate} can map the resulting context
 * directly into this object.
 */
@Entry(objectClasses = {"person", "top"}, base = "O=senate")
public final class SenateLdapPerson
{
    @Id
    private Name dn;

    @Attribute(name = "cn")
    @DnAttribute(value = "cn", index = 1)
    private String fullName;

    @DnAttribute(value = "ou", index = 0)
    private String organization;

    @Attribute(name = "employeeid")
    private String employeeId;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "uid")
    private String uid;

    @Attribute(name = "givenename")
    private String firstName;

    @Attribute(name = "middleinitial")
    private String middleInitial;

    @Attribute(name = "sn")
    private String sn;

    @Attribute(name = "title")
    private String title;

    @Attribute(name = "postaladdress")
    private String postalAddress;

    @Attribute(name = "officestreetaddress")
    private String officeAddress;

    @Attribute(name = "l")
    private String location;

    @Attribute(name = "st")
    private String state;

    @Attribute(name = "postalcode")
    private String postalCode;

    @Attribute(name = "department")
    private String department;

    @Attribute(name = "telephonenumber")
    private String phoneNumber;

    public SenateLdapPerson() {}

    /**
     * Getters/Setters
     */
    public Name getDn() {
        return dn;
    }

    public void setDn(Name dn) {
        this.dn = dn;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

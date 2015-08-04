package gov.nysenate.seta.model.attendance;

import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A TimeEntry contains all the hours worked and charged for a specific date.
 * TimeEntries are associated together via a common TimeRecord Id.
 */
public class TimeEntry
{
    protected BigInteger entryId;
    protected BigInteger timeRecordId;
    protected int empId;
    protected String employeeName;
    protected LocalDate date;
    protected BigDecimal workHours;
    protected BigDecimal travelHours;
    protected BigDecimal holidayHours;
    protected BigDecimal vacationHours;
    protected BigDecimal personalHours;
    protected BigDecimal sickEmpHours;
    protected BigDecimal sickFamHours;
    protected BigDecimal miscHours;
    protected MiscLeaveType miscType;
    protected boolean active;
    protected String empComment;
    protected PayType payType;
    protected String txOriginalUserId;
    protected String txUpdateUserId;
    protected LocalDateTime txOriginalDate;
    protected LocalDateTime txUpdateDate;

    /** --- Constructors --- */

    public TimeEntry() {}

    public TimeEntry(BigInteger timeRecordId, int empId) {
        this.timeRecordId = timeRecordId;
        this.empId = empId;
    }

    /** --- Functional Getters/Setters --- */

    public BigDecimal getDailyTotal() {
        return BigDecimal.ZERO
            .add(this.getWorkHours())
            .add(this.getTravelHours())
            .add(this.getHolidayHours())
            .add(this.getMiscHours())
            .add(this.getPersonalHours())
            .add(this.getSickEmpHours())
            .add(this.getSickFamHours())
            .add(this.getVacationHours());
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeEntry)) return false;
        TimeEntry timeEntry = (TimeEntry) o;
        return Objects.equals(empId, timeEntry.empId) &&
                Objects.equals(workHours, timeEntry.workHours) &&
                Objects.equals(travelHours, timeEntry.travelHours) &&
                Objects.equals(holidayHours, timeEntry.holidayHours) &&
                Objects.equals(vacationHours, timeEntry.vacationHours) &&
                Objects.equals(personalHours, timeEntry.personalHours) &&
                Objects.equals(sickEmpHours, timeEntry.sickEmpHours) &&
                Objects.equals(sickFamHours, timeEntry.sickFamHours) &&
                Objects.equals(miscHours, timeEntry.miscHours) &&
                Objects.equals(active, timeEntry.active) &&
                Objects.equals(entryId, timeEntry.entryId) &&
                Objects.equals(timeRecordId, timeEntry.timeRecordId) &&
                Objects.equals(employeeName, timeEntry.employeeName) &&
                Objects.equals(date, timeEntry.date) &&
                Objects.equals(miscType, timeEntry.miscType) &&
                Objects.equals(empComment, timeEntry.empComment) &&
                Objects.equals(payType, timeEntry.payType) &&
                Objects.equals(txOriginalUserId, timeEntry.txOriginalUserId) &&
                Objects.equals(txUpdateUserId, timeEntry.txUpdateUserId) &&
                Objects.equals(txOriginalDate, timeEntry.txOriginalDate) &&
                Objects.equals(txUpdateDate, timeEntry.txUpdateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId, timeRecordId, empId, employeeName, date, workHours, travelHours, holidayHours,
                            vacationHours, personalHours, sickEmpHours, sickFamHours, miscHours, miscType, active,
                            empComment, payType, txOriginalUserId, txUpdateUserId, txOriginalDate, txUpdateDate);
    }

    /** --- Basic Getters/Setters --- */

    public BigInteger getEntryId() {
        return entryId;
    }

    public void setEntryId(BigInteger entryId) {
        this.entryId = entryId;
    }

    public BigInteger getTimeRecordId() {
        return timeRecordId;
    }

    public void setTimeRecordId(BigInteger timeRecordId) {
        this.timeRecordId = timeRecordId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public BigDecimal getTravelHours() {
        return travelHours;
    }

    public void setTravelHours(BigDecimal travelHours) {
        this.travelHours = travelHours;
    }

    public BigDecimal getHolidayHours() {
        return holidayHours;
    }

    public void setHolidayHours(BigDecimal holidayHours) {
        this.holidayHours = holidayHours;
    }

    public BigDecimal getVacationHours() {
        return vacationHours;
    }

    public void setVacationHours(BigDecimal vacationHours) {
        this.vacationHours = vacationHours;
    }

    public BigDecimal getPersonalHours() {
        return personalHours;
    }

    public void setPersonalHours(BigDecimal personalHours) {
        this.personalHours = personalHours;
    }

    public BigDecimal getSickEmpHours() {
        return sickEmpHours;
    }

    public void setSickEmpHours(BigDecimal sickEmpHours) {
        this.sickEmpHours = sickEmpHours;
    }

    public BigDecimal getSickFamHours() {
        return sickFamHours;
    }

    public void setSickFamHours(BigDecimal sickFamHours) {
        this.sickFamHours = sickFamHours;
    }

    public BigDecimal getMiscHours() {
        return miscHours;
    }

    public void setMiscHours(BigDecimal miscHours) {
        this.miscHours = miscHours;
    }

    public MiscLeaveType getMiscType() {
        return miscType;
    }

    public void setMiscType(MiscLeaveType miscType) {
        this.miscType = miscType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmpComment() {
        return empComment;
    }

    public void setEmpComment(String empComment) {
        this.empComment = empComment;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public String getTxOriginalUserId() {
        return txOriginalUserId;
    }

    public void setTxOriginalUserId(String txOriginalUserId) {
        this.txOriginalUserId = txOriginalUserId;
    }

    public String getTxUpdateUserId() {
        return txUpdateUserId;
    }

    public void setTxUpdateUserId(String txUpdateUserId) {
        this.txUpdateUserId = txUpdateUserId;
    }

    public LocalDateTime getTxOriginalDate() {
        return txOriginalDate;
    }

    public void setTxOriginalDate(LocalDateTime txOriginalDate) {
        this.txOriginalDate = txOriginalDate;
    }

    public LocalDateTime getTxUpdateDate() {
        return txUpdateDate;
    }

    public void setTxUpdateDate(LocalDateTime txUpdateDate) {
        this.txUpdateDate = txUpdateDate;
    }
}
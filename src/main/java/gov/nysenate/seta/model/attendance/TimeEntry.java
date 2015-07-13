package gov.nysenate.seta.model.attendance;

import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;

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
    protected int workHours;
    protected int travelHours;
    protected int holidayHours;
    protected int vacationHours;
    protected int personalHours;
    protected int sickEmpHours;
    protected int sickFamHours;
    protected int miscHours;
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

    public int getDailyTotal() {
        int total = 0;
        total += this.getWorkHours();
        total += this.getTravelHours();
        total += this.getHolidayHours();
        total += this.getVacationHours();
        total += this.getPersonalHours();
        total += this.getSickEmpHours();
        total += this.getSickFamHours();
        total += this.getMiscHours();
        return total;
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

    public int getWorkHours() {
        return workHours;
    }

    public void setWorkHours(int workHours) {
        this.workHours = workHours;
    }

    public int getTravelHours() {
        return travelHours;
    }

    public void setTravelHours(int travelHours) {
        this.travelHours = travelHours;
    }

    public int getHolidayHours() {
        return holidayHours;
    }

    public void setHolidayHours(int holidayHours) {
        this.holidayHours = holidayHours;
    }

    public int getVacationHours() {
        return vacationHours;
    }

    public void setVacationHours(int vacationHours) {
        this.vacationHours = vacationHours;
    }

    public int getPersonalHours() {
        return personalHours;
    }

    public void setPersonalHours(int personalHours) {
        this.personalHours = personalHours;
    }

    public int getSickEmpHours() {
        return sickEmpHours;
    }

    public void setSickEmpHours(int sickEmpHours) {
        this.sickEmpHours = sickEmpHours;
    }

    public int getSickFamHours() {
        return sickFamHours;
    }

    public void setSickFamHours(int sickFamHours) {
        this.sickFamHours = sickFamHours;
    }

    public int getMiscHours() {
        return miscHours;
    }

    public void setMiscHours(int miscHours) {
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
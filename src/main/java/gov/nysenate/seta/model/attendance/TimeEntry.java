package gov.nysenate.seta.model.attendance;

import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeEntry)) return false;

        TimeEntry timeEntry = (TimeEntry) o;

        if (active != timeEntry.active) return false;
        if (empId != timeEntry.empId) return false;
        if (holidayHours != timeEntry.holidayHours) return false;
        if (miscHours != timeEntry.miscHours) return false;
        if (personalHours != timeEntry.personalHours) return false;
        if (sickEmpHours != timeEntry.sickEmpHours) return false;
        if (sickFamHours != timeEntry.sickFamHours) return false;
        if (travelHours != timeEntry.travelHours) return false;
        if (vacationHours != timeEntry.vacationHours) return false;
        if (workHours != timeEntry.workHours) return false;
        if (!date.equals(timeEntry.date)) return false;
        if (empComment != null ? !empComment.equals(timeEntry.empComment) : timeEntry.empComment != null) return false;
        if (!employeeName.equals(timeEntry.employeeName)) return false;
        if (entryId != null ? !entryId.equals(timeEntry.entryId) : timeEntry.entryId != null) return false;
        if (miscType != timeEntry.miscType) return false;
        if (payType != timeEntry.payType) return false;
        if (timeRecordId != null ? !timeRecordId.equals(timeEntry.timeRecordId) : timeEntry.timeRecordId != null)
            return false;
        if (txOriginalDate != null ? !txOriginalDate.equals(timeEntry.txOriginalDate) : timeEntry.txOriginalDate != null)
            return false;
        if (txOriginalUserId != null ? !txOriginalUserId.equals(timeEntry.txOriginalUserId) : timeEntry.txOriginalUserId != null)
            return false;
        if (txUpdateDate != null ? !txUpdateDate.equals(timeEntry.txUpdateDate) : timeEntry.txUpdateDate != null)
            return false;
        if (txUpdateUserId != null ? !txUpdateUserId.equals(timeEntry.txUpdateUserId) : timeEntry.txUpdateUserId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entryId != null ? entryId.hashCode() : 0;
        result = 31 * result + (timeRecordId != null ? timeRecordId.hashCode() : 0);
        result = 31 * result + empId;
        result = 31 * result + employeeName.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + workHours;
        result = 31 * result + travelHours;
        result = 31 * result + holidayHours;
        result = 31 * result + vacationHours;
        result = 31 * result + personalHours;
        result = 31 * result + sickEmpHours;
        result = 31 * result + sickFamHours;
        result = 31 * result + miscHours;
        result = 31 * result + (miscType != null ? miscType.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (empComment != null ? empComment.hashCode() : 0);
        result = 31 * result + (payType != null ? payType.hashCode() : 0);
        result = 31 * result + (txOriginalUserId != null ? txOriginalUserId.hashCode() : 0);
        result = 31 * result + (txUpdateUserId != null ? txUpdateUserId.hashCode() : 0);
        result = 31 * result + (txOriginalDate != null ? txOriginalDate.hashCode() : 0);
        result = 31 * result + (txUpdateDate != null ? txUpdateDate.hashCode() : 0);
        return result;
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
package gov.nysenate.seta.model.attendance;

import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * A TimeEntry contains all the hours worked and charged for a specific date.
 * TimeEntries are associated together via a common TimeRecord Id.
 */
public class TimeEntry
{
    protected String entryId;
    protected String timeRecordId;
    protected Integer empId;
    protected Date date;
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
    protected Date txOriginalDate;
    protected Date txUpdateDate;

    /** --- Constructors --- */

    public TimeEntry() {}

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeEntry)) return false;

        TimeEntry timeEntry = (TimeEntry) o;

        if (active != timeEntry.active) return false;
        if (!date.equals(timeEntry.date)) return false;
        if (empComment != null ? !empComment.equals(timeEntry.empComment) : timeEntry.empComment != null) return false;
        if (empId != null ? !empId.equals(timeEntry.empId) : timeEntry.empId != null) return false;
        if (entryId != null ? !entryId.equals(timeEntry.entryId) : timeEntry.entryId != null) return false;
        if (holidayHours != null ? !holidayHours.equals(timeEntry.holidayHours) : timeEntry.holidayHours != null)
            return false;
        if (miscHours != null ? !miscHours.equals(timeEntry.miscHours) : timeEntry.miscHours != null) return false;
        if (miscType != timeEntry.miscType) return false;
        if (payType != timeEntry.payType) return false;
        if (personalHours != null ? !personalHours.equals(timeEntry.personalHours) : timeEntry.personalHours != null)
            return false;
        if (sickEmpHours != null ? !sickEmpHours.equals(timeEntry.sickEmpHours) : timeEntry.sickEmpHours != null)
            return false;
        if (sickFamHours != null ? !sickFamHours.equals(timeEntry.sickFamHours) : timeEntry.sickFamHours != null)
            return false;
        if (timeRecordId != null ? !timeRecordId.equals(timeEntry.timeRecordId) : timeEntry.timeRecordId != null)
            return false;
        if (travelHours != null ? !travelHours.equals(timeEntry.travelHours) : timeEntry.travelHours != null)
            return false;
        if (txOriginalDate != null ? !txOriginalDate.equals(timeEntry.txOriginalDate) : timeEntry.txOriginalDate != null)
            return false;
        if (txOriginalUserId != null ? !txOriginalUserId.equals(timeEntry.txOriginalUserId) : timeEntry.txOriginalUserId != null)
            return false;
        if (txUpdateDate != null ? !txUpdateDate.equals(timeEntry.txUpdateDate) : timeEntry.txUpdateDate != null)
            return false;
        if (txUpdateUserId != null ? !txUpdateUserId.equals(timeEntry.txUpdateUserId) : timeEntry.txUpdateUserId != null)
            return false;
        if (vacationHours != null ? !vacationHours.equals(timeEntry.vacationHours) : timeEntry.vacationHours != null)
            return false;
        if (workHours != null ? !workHours.equals(timeEntry.workHours) : timeEntry.workHours != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entryId != null ? entryId.hashCode() : 0;
        result = 31 * result + (timeRecordId != null ? timeRecordId.hashCode() : 0);
        result = 31 * result + (empId != null ? empId.hashCode() : 0);
        result = 31 * result + date.hashCode();
        result = 31 * result + (workHours != null ? workHours.hashCode() : 0);
        result = 31 * result + (travelHours != null ? travelHours.hashCode() : 0);
        result = 31 * result + (holidayHours != null ? holidayHours.hashCode() : 0);
        result = 31 * result + (vacationHours != null ? vacationHours.hashCode() : 0);
        result = 31 * result + (personalHours != null ? personalHours.hashCode() : 0);
        result = 31 * result + (sickEmpHours != null ? sickEmpHours.hashCode() : 0);
        result = 31 * result + (sickFamHours != null ? sickFamHours.hashCode() : 0);
        result = 31 * result + (miscHours != null ? miscHours.hashCode() : 0);
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

    public BigDecimal getDailyTotal() {
        BigDecimal total = new BigDecimal(0);
        total = total.add(getWorkHours());
        total = total.add(getTravelHours());
        total = total.add(getHolidayHours());
        total = total.add(getVacationHours());
        total = total.add(getPersonalHours());
        total = total.add(getSickEmpHours());
        total = total.add(getSickFamHours());
        total = total.add(getMiscHours());
        return total;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = (workHours != null) ? workHours : BigDecimal.ZERO;
    }

    public void setTravelHours(BigDecimal travelHours) {
        this.travelHours = (travelHours != null) ? travelHours : BigDecimal.ZERO;
    }

    public void setHolidayHours(BigDecimal holidayHours) {
        this.holidayHours = (holidayHours != null) ? holidayHours : BigDecimal.ZERO;
    }

    public void setVacationHours(BigDecimal vacationHours) {
        this.vacationHours = (vacationHours != null) ? vacationHours : BigDecimal.ZERO;
    }

    public void setPersonalHours(BigDecimal personalHours) {
        this.personalHours = (personalHours != null) ? personalHours : BigDecimal.ZERO;
    }

    public void setSickEmpHours(BigDecimal sickEmpHours) {
        this.sickEmpHours = (sickEmpHours != null) ? sickEmpHours : BigDecimal.ZERO;
    }

    public void setSickFamHours(BigDecimal sickFamHours) {
        this.sickFamHours = (sickFamHours != null) ? sickFamHours : BigDecimal.ZERO;
    }

    public void setMiscHours(BigDecimal miscHours) {
        this.miscHours = (miscHours != null) ? miscHours : BigDecimal.ZERO;
    }

    /** --- Basic Getters/Setters --- */

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getTimeRecordId() {
        return timeRecordId;
    }

    public void setTimeRecordId(String timeRecordId) {
        this.timeRecordId = timeRecordId;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public BigDecimal getTravelHours() {
        return travelHours;
    }

    public BigDecimal getHolidayHours() {
        return holidayHours;
    }

    public BigDecimal getVacationHours() {
        return vacationHours;
    }

    public BigDecimal getPersonalHours() {
        return personalHours;
    }

    public BigDecimal getSickEmpHours() {
        return sickEmpHours;
    }

    public BigDecimal getSickFamHours() {
        return sickFamHours;
    }

    public BigDecimal getMiscHours() {
        return miscHours;
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

    public Date getTxOriginalDate() {
        return txOriginalDate;
    }

    public void setTxOriginalDate(Timestamp txOriginalDate) {
        this.txOriginalDate = txOriginalDate;
    }

    public Date getTxUpdateDate() {
        return txUpdateDate;
    }

    public void setTxUpdateDate(Timestamp txUpdateDate) {
        this.txUpdateDate = txUpdateDate;
    }
}
package gov.nysenate.seta.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Represents the time entry for a specific date.
 */
public class TimeEntry
{
    protected int tDayId;
    protected int timesheetId;
    protected int empId;
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
    protected int tOriginalUserId;
    protected int tUpdateUserId;
    protected Timestamp tOriginalDate;
    protected Timestamp tUpdateDate;

    public TimeEntry() {}

    /** Functional Getters */

    public BigDecimal getDailyTotal() {

        BigDecimal TotalHours = new BigDecimal("0.00");

        BigDecimal result = TotalHours.add(getWorkHours());
        result.add(getHolidayHours());
        result.add(getVacationHours());
        result.add(getPersonalHours());
        result.add(getSickEmpHours());
        result.add(getSickFamHours());
        result.add(getMiscHours());
        return result;
    }

   /** Basic Getters/Setters */

    public int gettDayId() {
        return tDayId;
    }

    public void settDayId(int tDayId) {
        this.tDayId = tDayId;
    }

    public int getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(int timesheetId) {
        this.timesheetId = timesheetId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
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

    public int gettOriginalUserId() {
        return tOriginalUserId;
    }

    public void settOriginalUserId(int tOriginalUserId) {
        this.tOriginalUserId = tOriginalUserId;
    }

    public int gettUpdateUserId() {
        return tUpdateUserId;
    }

    public void settUpdateUserId(int tUpdateUserId) {
        this.tUpdateUserId = tUpdateUserId;
    }

    public Timestamp gettOriginalDate() {
        return tOriginalDate;
    }

    public void settOriginalDate(Timestamp tOriginalDate) {
        this.tOriginalDate = tOriginalDate;
    }

    public Timestamp gettUpdateDate() {
        return tUpdateDate;
    }

    public void settUpdateDate(Timestamp tUpdateDate) {
        this.tUpdateDate = tUpdateDate;
    }
}

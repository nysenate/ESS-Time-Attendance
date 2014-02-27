package gov.nysenate.seta.model;

import java.util.Date;

/**
 * Represents the time entry for a specific date.
 */
public class TimeEntry
{
    protected Date date;
    protected int workHours;
    protected int holidayHours;
    protected int vacationHours;
    protected int personalHours;
    protected int sickEmpHours;
    protected int sickFamHours;
    protected int miscHours;
    protected MiscLeaveType miscType;

    public TimeEntry() {}

    /** Functional Getters */

    public int getDailyTotal() {
        return getWorkHours() + getHolidayHours() + getVacationHours() + getPersonalHours() +
               getSickEmpHours() + getSickFamHours() + getMiscHours();
    }

    /** Basic Getters/Setters */

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getWorkHours() {
        return workHours;
    }

    public void setWorkHours(int workHours) {
        this.workHours = workHours;
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
}

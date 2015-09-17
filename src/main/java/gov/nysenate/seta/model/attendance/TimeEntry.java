package gov.nysenate.seta.model.attendance;

import com.google.common.base.Objects;
import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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
    protected String originalUserId;
    protected String updateUserId;
    protected LocalDateTime originalDate;
    protected LocalDateTime updateDate;

    /** --- Constructors --- */

    public TimeEntry() {}

    public TimeEntry(BigInteger timeRecordId, int empId) {
        this.timeRecordId = timeRecordId;
        this.empId = empId;
    }

    public TimeEntry(TimeRecord record, PayType payType, LocalDate date) {
        this.timeRecordId = record.getTimeRecordId();
        this.empId = record.getEmployeeId();
        this.employeeName = record.getEmployeeName();
        this.date = date;
        this.active = true;
        this.payType = payType;
        this.originalUserId = record.getOriginalUserId();
        this.updateUserId = this.originalUserId;
        this.originalDate = LocalDateTime.now();
        this.updateDate = this.originalDate;
    }

    public TimeEntry(TimeEntry other) {
        this.entryId = other.entryId;
        this.timeRecordId = other.timeRecordId;
        this.empId = other.empId;
        this.employeeName = other.employeeName;
        this.date = other.date;
        this.workHours = other.workHours;
        this.travelHours = other.travelHours;
        this.holidayHours = other.holidayHours;
        this.vacationHours = other.vacationHours;
        this.personalHours = other.personalHours;
        this.sickEmpHours = other.sickEmpHours;
        this.sickFamHours = other.sickFamHours;
        this.miscHours = other.miscHours;
        this.miscType = other.miscType;
        this.active = other.active;
        this.empComment = other.empComment;
        this.payType = other.payType;
        this.originalUserId = other.originalUserId;
        this.updateUserId = other.updateUserId;
        this.originalDate = other.originalDate;
        this.updateDate = other.updateDate;
    }


    /** --- Functional Getters/Setters --- */

    public BigDecimal getDailyTotal() {
        return BigDecimal.ZERO
            .add(this.getWorkHours().orElse(BigDecimal.ZERO))
            .add(this.getTravelHours().orElse(BigDecimal.ZERO))
            .add(this.getHolidayHours().orElse(BigDecimal.ZERO))
            .add(this.getMiscHours().orElse(BigDecimal.ZERO))
            .add(this.getPersonalHours().orElse(BigDecimal.ZERO))
            .add(this.getSickEmpHours().orElse(BigDecimal.ZERO))
            .add(this.getSickFamHours().orElse(BigDecimal.ZERO))
            .add(this.getVacationHours().orElse(BigDecimal.ZERO));
    }

    public Optional<BigDecimal> getWorkHours() {
        return Optional.ofNullable(workHours);
    }

    public Optional<BigDecimal> getTravelHours() {
        return Optional.ofNullable(travelHours);
    }

    public Optional<BigDecimal> getHolidayHours() {
        return Optional.ofNullable(holidayHours);
    }

    public Optional<BigDecimal> getVacationHours() {
        return Optional.ofNullable(vacationHours);
    }

    public Optional<BigDecimal> getPersonalHours() {
        return Optional.ofNullable(personalHours);
    }

    public Optional<BigDecimal> getSickEmpHours() {
        return Optional.ofNullable(sickEmpHours);
    }

    public Optional<BigDecimal> getSickFamHours() {
        return Optional.ofNullable(sickFamHours);
    }

    public Optional<BigDecimal> getMiscHours() {
        return Optional.ofNullable(miscHours);
    }

    /**
     * @return true if no fields are set for this time record
     */
    public boolean isEmpty() {
        return workHours == null &&
                travelHours == null &&
                holidayHours == null &&
                vacationHours == null &&
                personalHours == null &&
                sickEmpHours == null &&
                sickFamHours == null &&
                miscHours == null &&
                miscType == null &&
                empComment == null;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeEntry)) return false;
        TimeEntry entry = (TimeEntry) o;
        return Objects.equal(active, entry.active) &&
                Objects.equal(timeRecordId, entry.timeRecordId) &&
                Objects.equal(date, entry.date) &&
                Objects.equal(workHours, entry.workHours) &&
                Objects.equal(travelHours, entry.travelHours) &&
                Objects.equal(holidayHours, entry.holidayHours) &&
                Objects.equal(vacationHours, entry.vacationHours) &&
                Objects.equal(personalHours, entry.personalHours) &&
                Objects.equal(sickEmpHours, entry.sickEmpHours) &&
                Objects.equal(sickFamHours, entry.sickFamHours) &&
                Objects.equal(miscHours, entry.miscHours) &&
                Objects.equal(miscType, entry.miscType) &&
                Objects.equal(empComment, entry.empComment) &&
                Objects.equal(payType, entry.payType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timeRecordId, date, workHours, travelHours, holidayHours, vacationHours, personalHours,
                sickEmpHours, sickFamHours, miscHours, miscType, active, empComment, payType);
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

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public void setTravelHours(BigDecimal travelHours) {
        this.travelHours = travelHours;
    }

    public void setHolidayHours(BigDecimal holidayHours) {
        this.holidayHours = holidayHours;
    }

    public void setVacationHours(BigDecimal vacationHours) {
        this.vacationHours = vacationHours;
    }

    public void setPersonalHours(BigDecimal personalHours) {
        this.personalHours = personalHours;
    }

    public void setSickEmpHours(BigDecimal sickEmpHours) {
        this.sickEmpHours = sickEmpHours;
    }

    public void setSickFamHours(BigDecimal sickFamHours) {
        this.sickFamHours = sickFamHours;
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

    public String getOriginalUserId() {
        return originalUserId;
    }

    public void setOriginalUserId(String originalUserId) {
        this.originalUserId = originalUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public LocalDateTime getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(LocalDateTime originalDate) {
        this.originalDate = originalDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
package gov.nysenate.seta.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.String;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@XmlRootElement
public class TimeEntryView implements ViewObject {

    protected String entryId;
    protected String timeRecordId;
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
    protected String miscType;
    protected boolean active;
    protected String empComment;
    protected String payType;
    protected String txOriginalUserId;
    protected String txUpdateUserId;
    protected LocalDateTime txOriginalDate;
    protected LocalDateTime txUpdateDate;

    public TimeEntryView() {}

    public TimeEntryView(TimeEntry entry) {
        if (entry != null) {
            this.entryId = entry.getEntryId() != null ? entry.getEntryId().toString() : null;
            this.timeRecordId = entry.getEntryId() != null ? entry.getTimeRecordId().toString() : null;
            this.empId = entry.getEmpId();
            this.employeeName = entry.getEmployeeName();
            this.date = entry.getDate();
            this.workHours = entry.getWorkHours();
            this.travelHours = entry.getTravelHours();
            this.holidayHours = entry.getHolidayHours();
            this.vacationHours = entry.getVacationHours();
            this.personalHours = entry.getPersonalHours();
            this.sickEmpHours = entry.getSickEmpHours();
            this.sickFamHours = entry.getSickFamHours();
            this.miscHours = entry.getMiscHours();
            this.miscType = entry.getMiscType() != null ? entry.getMiscType().name() : null;
            this.active = entry.isActive();
            this.empComment = entry.getEmpComment();
            this.payType = entry.getPayType() != null ? entry.getPayType().name() : null;
            this.txOriginalUserId = entry.getTxOriginalUserId();
            this.txUpdateUserId = entry.getTxUpdateUserId();
            this.txOriginalDate = entry.getTxOriginalDate();
            this.txUpdateDate = entry.getTxUpdateDate();
        }
    }

    @JsonIgnore
    public TimeEntry toTimeEntry() {
        TimeEntry entry = new TimeEntry(new BigInteger(timeRecordId), empId);
        entry.setEntryId(new BigInteger(entryId));
        entry.setEmployeeName(employeeName);
        entry.setDate(date);
        entry.setWorkHours(workHours);
        entry.setTravelHours(travelHours);
        entry.setHolidayHours(holidayHours);
        entry.setVacationHours(vacationHours);
        entry.setPersonalHours(personalHours);
        entry.setSickEmpHours(sickEmpHours);
        entry.setSickFamHours(sickFamHours);
        entry.setMiscHours(miscHours);
        entry.setMiscType(miscType != null ? MiscLeaveType.valueOf(miscType) : null);
        entry.setActive(active);
        entry.setEmpComment(empComment);
        entry.setPayType(payType != null ? PayType.valueOf(payType) : null);
        entry.setTxOriginalUserId(txOriginalUserId);
        entry.setTxUpdateUserId(txUpdateUserId);
        entry.setTxOriginalDate(txOriginalDate);
        entry.setTxUpdateDate(txUpdateDate);
        return entry;
    }

    @XmlElement
    public String getEntryId() {
        return entryId;
    }

    @XmlElement
    public String getTimeRecordId() {
        return timeRecordId;
    }

    @XmlElement
    public int getEmpId() {
        return empId;
    }

    @XmlElement
    public String getEmployeeName() {
        return employeeName;
    }

    @XmlElement
    public LocalDate getDate() {
        return date;
    }

    @XmlElement
    public int getWorkHours() {
        return workHours;
    }

    @XmlElement
    public int getTravelHours() {
        return travelHours;
    }

    @XmlElement
    public int getHolidayHours() {
        return holidayHours;
    }

    @XmlElement
    public int getVacationHours() {
        return vacationHours;
    }

    @XmlElement
    public int getPersonalHours() {
        return personalHours;
    }

    @XmlElement
    public int getSickEmpHours() {
        return sickEmpHours;
    }

    @XmlElement
    public int getSickFamHours() {
        return sickFamHours;
    }

    @XmlElement
    public int getMiscHours() {
        return miscHours;
    }

    @XmlElement
    public String getMiscType() {
        return miscType;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public String getEmpComment() {
        return empComment;
    }

    @XmlElement
    public String getPayType() {
        return payType;
    }

    @XmlElement
    public String getTxOriginalUserId() {
        return txOriginalUserId;
    }

    @XmlElement
    public String getTxUpdateUserId() {
        return txUpdateUserId;
    }

    @XmlElement
    public LocalDateTime getTxOriginalDate() {
        return txOriginalDate;
    }

    @XmlElement
    public LocalDateTime getTxUpdateDate() {
        return txUpdateDate;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "time entry";
    }
}

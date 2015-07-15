package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@XmlRootElement(name = "timeEntry")
public class TimeEntryView implements ViewObject {

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

    public TimeEntryView(TimeEntry entry) {
        if (entry != null) {
            this.entryId = entry.getEntryId();
            this.timeRecordId = entry.getTimeRecordId();
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
            this.miscType = entry.getMiscType();
            this.active = entry.isActive();
            this.empComment = entry.getEmpComment();
            this.payType = entry.getPayType();
            this.txOriginalUserId = entry.getTxOriginalUserId();
            this.txUpdateUserId = entry.getTxUpdateUserId();
            this.txOriginalDate = entry.getTxOriginalDate();
            this.txUpdateDate = entry.getTxUpdateDate();
        }
    }

    @XmlElement
    public BigInteger getEntryId() {
        return entryId;
    }

    @XmlElement
    public BigInteger getTimeRecordId() {
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
    public MiscLeaveType getMiscType() {
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
    public PayType getPayType() {
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

package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement
public class TimeRecordView implements ViewObject {

    protected BigInteger timeRecordId;
    protected Integer employeeId;
    protected Integer supervisorId;
    protected String employeeName;
    protected boolean active;
    protected PayPeriodView payPeriod;
    protected String remarks;
    protected String exceptionDetails;
    protected LocalDate processedDate;
    protected TimeRecordStatus recordStatus;
    protected String txOriginalUserId;
    protected String txUpdateUserId;
    protected LocalDateTime txOriginalDate;
    protected LocalDateTime txUpdateDate;
    protected List<TimeEntryView> timeEntries;

    public TimeRecordView(TimeRecord record) {
        if (record != null) {
            this.timeRecordId = record.getTimeRecordId();
            this.employeeId = record.getEmployeeId();
            this.supervisorId = record.getSupervisorId();
            this.employeeName = record.getEmployeeName();
            this.active = record.isActive();
            this.payPeriod = new PayPeriodView(record.getPayPeriod());
            this.remarks = record.getRemarks();
            this.exceptionDetails = record.getExceptionDetails();
            this.processedDate = record.getProcessedDate();
            this.recordStatus = record.getRecordStatus();
            this.txOriginalUserId = record.getTxOriginalUserId();
            this.txUpdateUserId = record.getTxUpdateUserId();
            this.txOriginalDate = record.getTxOriginalDate();
            this.txUpdateDate = record.getTxUpdateDate();
            this.timeEntries = record.getTimeEntries().stream()
                    .map(TimeEntryView::new)
                    .collect(Collectors.toList());
        }
    }

    @XmlElement
    public BigInteger getTimeRecordId() {
        return timeRecordId;
    }

    @XmlElement
    public Integer getEmployeeId() {
        return employeeId;
    }

    @XmlElement
    public Integer getSupervisorId() {
        return supervisorId;
    }

    @XmlElement
    public String getEmployeeName() {
        return employeeName;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public PayPeriodView getPayPeriod() {
        return payPeriod;
    }

    @XmlElement
    public String getRemarks() {
        return remarks;
    }

    @XmlElement
    public String getExceptionDetails() {
        return exceptionDetails;
    }

    @XmlElement
    public LocalDate getProcessedDate() {
        return processedDate;
    }

    @XmlElement
    public TimeRecordStatus getRecordStatus() {
        return recordStatus;
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

    @XmlElement
    public List<TimeEntryView> getTimeEntries() {
        return timeEntries;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "time record";
    }
}

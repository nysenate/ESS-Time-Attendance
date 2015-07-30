package gov.nysenate.seta.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@XmlRootElement(name = "timeRecord")
public class TimeRecordView implements ViewObject {

    protected String timeRecordId;
    protected Integer employeeId;
    protected Integer supervisorId;
    protected String scope;
    protected String employeeName;
    protected String respHeadCode;
    protected boolean active;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected String remarks;
    protected String exceptionDetails;
    protected LocalDate processedDate;
    protected String recordStatus;
    protected String txOriginalUserId;
    protected String txUpdateUserId;
    protected LocalDateTime txOriginalDate;
    protected LocalDateTime txUpdateDate;
    protected List<TimeEntryView> timeEntries;

    public TimeRecordView() {}

    public TimeRecordView(TimeRecord record) {
        if (record != null) {
            this.timeRecordId = String.valueOf(record.getTimeRecordId());
            this.employeeId = record.getEmployeeId();
            this.supervisorId = record.getSupervisorId();
            this.employeeName = record.getEmployeeName();
            this.respHeadCode = record.getRespHeadCode();
            this.scope = (record.getRecordStatus() != null) ? record.getRecordStatus().getScope().getCode() : null;
            this.active = record.isActive();
            this.beginDate = record.getBeginDate();
            this.endDate = record.getEndDate();
            this.remarks = record.getRemarks();
            this.exceptionDetails = record.getExceptionDetails();
            this.processedDate = record.getProcessedDate();
            this.recordStatus = record.getRecordStatus() != null ? record.getRecordStatus().name() : null;
            this.txOriginalUserId = record.getTxOriginalUserId();
            this.txUpdateUserId = record.getTxUpdateUserId();
            this.txOriginalDate = record.getTxOriginalDate();
            this.txUpdateDate = record.getTxUpdateDate();
            this.timeEntries = record.getTimeEntries().stream()
                    .map(TimeEntryView::new)
                    .collect(Collectors.toList());
        }
    }

    @JsonIgnore
    public TimeRecord toTimeRecord() {
        TimeRecord record = new TimeRecord();
        record.setTimeRecordId(new BigInteger(timeRecordId));
        record.setEmployeeId(employeeId);
        record.setSupervisorId(supervisorId);
        record.setEmployeeName(employeeName);
        record.setRespHeadCode(respHeadCode);
        record.setActive(active);
        record.setBeginDate(beginDate);
        record.setEndDate(endDate);
        record.setRemarks(remarks);
        record.setExceptionDetails(exceptionDetails);
        record.setProcessedDate(processedDate);
        record.setRecordStatus(recordStatus != null ? TimeRecordStatus.valueOf(recordStatus) : null);
        record.setTxOriginalUserId(txOriginalUserId);
        record.setTxUpdateUserId(txUpdateUserId);
        record.setTxOriginalDate(txOriginalDate);
        record.setTxUpdateDate(txUpdateDate);
        record.setTimeEntries(timeEntries.stream()
                .map(TimeEntryView::toTimeEntry)
                .collect(Collectors.toList()));
        return record;
    }

    @XmlElement
    public String getTimeRecordId() {
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
    public String getScope() {
        return scope;
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
    public LocalDate getBeginDate() {
        return beginDate;
    }

    @XmlElement
    public LocalDate getEndDate() {
        return endDate;
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
    public String getRecordStatus() {
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

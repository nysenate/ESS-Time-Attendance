package gov.nysenate.seta.model.attendance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A Time Record is the biweekly collection of daily time entries. The time record
 * is typically created in accordance with the attendance pay periods.
 */
public class TimeRecord
{
    protected String timeRecordId;
    protected Integer employeeId;
    protected Integer supervisorId;
    protected boolean active;
    protected Date beginDate;
    protected Date endDate;
    protected String remarks;
    protected String exceptionDetails;
    protected Date processedDate;
    protected TimeRecordStatus recordStatus;
    protected String txOriginalUserId;
    protected String txUpdateUserId;
    protected Date txOriginalDate;
    protected Date txUpdateDate;
    protected List<TimeEntry> timeEntries;

    /** --- Constructors --- */

    public TimeRecord() {
        this.timeEntries = new ArrayList<>();
    }

    /** --- Basic Getters/Setters --- */

    public String getTimeRecordId() {
        return timeRecordId;
    }

    public void setTimeRecordId(String timeRecordId) {
        this.timeRecordId = timeRecordId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Integer supervisorId) {
        this.supervisorId = supervisorId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getExceptionDetails() {
        return exceptionDetails;
    }

    public void setExceptionDetails(String exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public TimeRecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(TimeRecordStatus recordStatus) {
        this.recordStatus = recordStatus;
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

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntry> timeEntries) {
        this.timeEntries = timeEntries;
    }
}
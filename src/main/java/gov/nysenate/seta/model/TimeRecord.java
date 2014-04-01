package gov.nysenate.seta.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by riken on 3/4/14.
 */
public class TimeRecord {

    protected BigDecimal timesheetId;
    protected BigDecimal employeeId;
    protected String tOriginalUserId;
    protected String tUpdateUserId;
    protected Timestamp tOriginalDate;
    protected Timestamp tUpdateDate;
    protected BigDecimal supervisorId;
    protected boolean active;
    protected Date beginDate;
    protected Date endDate;
    protected String remarks;
    protected String exeDetails;
    protected Date proDate;
    protected TimeRecordStatus recordStatus;

    /** Getters and Setters **/

    public BigDecimal getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(BigDecimal timesheetId) {
        this.timesheetId = timesheetId;
    }

    public BigDecimal getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(BigDecimal employeeId) {
        this.employeeId = employeeId;
    }

    public String gettOriginalUserId() {
        return tOriginalUserId;
    }

    public void settOriginalUserId(String tOriginalUserId) {
        this.tOriginalUserId = tOriginalUserId;
    }

    public String gettUpdateUserId() {
        return tUpdateUserId;
    }

    public void settUpdateUserId(String tUpdateUserId) {
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

    public BigDecimal getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(BigDecimal supervisorId) {
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

    public String getExeDetails() {
        return exeDetails;
    }

    public void setExeDetails(String exeDetails) {
        this.exeDetails = exeDetails;
    }

    public Date getProDate() {
        return proDate;
    }

    public void setProDate(Date proDate) {
        this.proDate = proDate;
    }

    public TimeRecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(TimeRecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

}

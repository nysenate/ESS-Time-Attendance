package gov.nysenate.seta.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by riken on 3/4/14.
 */
public class TimeRecord {

    protected int timesheetId;
    protected int employeeId;
    protected int tOriginalUserId;
    protected int tUpdateUserId;
    protected Timestamp tOriginalDate;
    protected Timestamp tUpdateDate;
    protected int supervisorId;
    protected boolean active;
    protected Date beginDate;
    protected Date endDate;
    protected String remarks;
    protected String exeDetails;
    protected Date proDate;
    protected PayType payType;
    protected TimeRecordStatus recordStatus;

    /** Getters and Setters **/

    public int getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(int timesheetId) {
        this.timesheetId = timesheetId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
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

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
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

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }
}

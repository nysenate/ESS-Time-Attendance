package gov.nysenate.seta.model.attendance;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.model.accrual.PeriodAccUsage;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.personnel.Employee;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A Time Record is the biweekly collection of daily time entries. The time record
 * is typically created in accordance with the attendance pay periods.
 */
public class TimeRecord implements Comparable<TimeRecord>
{
    protected BigInteger timeRecordId;
    protected Integer employeeId;
    protected Integer supervisorId;
    protected String employeeName;
    protected String respHeadCode;
    protected boolean active;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected PayPeriod payPeriod;
    protected String remarks;
    protected String exceptionDetails;
    protected LocalDate processedDate;
    protected TimeRecordStatus recordStatus;
    protected String txOriginalUserId;
    protected String txUpdateUserId;
    protected LocalDateTime txOriginalDate;
    protected LocalDateTime txUpdateDate;
    protected List<TimeEntry> timeEntries = new ArrayList<>();

    /** --- Constructors --- */

    public TimeRecord() {}

    public TimeRecord(Employee employee, Range<LocalDate> dateRange, PayPeriod payPeriod, int supervisorId) {
        this.employeeId = employee.getEmployeeId();
        this.supervisorId = supervisorId;
        this.employeeName = employee.getUid().toUpperCase();
        this.respHeadCode = employee.getRespCenter().getHead().getCode();
        this.active = true;
        this.beginDate = DateUtils.startOfDateRange(dateRange);
        this.endDate = DateUtils.endOfDateRange(dateRange);
        this.payPeriod = payPeriod;
        this.recordStatus = TimeRecordStatus.NOT_SUBMITTED;
        this.txOriginalUserId = this.employeeName;
        this.txUpdateUserId = this.employeeName;
        this.txOriginalDate = LocalDateTime.now();
        this.txUpdateDate = txOriginalDate;
    }

    @Override
    public int compareTo(TimeRecord o) {
        return ComparisonChain.start()
                .compare(this.beginDate, o.beginDate)
                .compare(this.endDate, o.endDate)
                .compare(this.employeeId, o.employeeId)
                .result();
    }

    /** --- Functions --- */

    public boolean encloses(PayPeriod period) {
        return Range.closed(beginDate, endDate).encloses(period.getDateRange());
    }

    /** --- Functional Getters / Setters --- */

    public Range<LocalDate> getDateRange() {
        return Range.closed(beginDate, endDate);
    }

    /**
     * Constructs and returns a PeriodAccUsage by summing the values from the time entries.
     * @return PeriodAccUsage
     */
    public PeriodAccUsage getPeriodAccUsage() {
        PeriodAccUsage usage = new PeriodAccUsage();
        usage.setEmpId(employeeId);
        usage.setPayPeriod(payPeriod);
        usage.setYear(payPeriod.getEndDate().getYear());
        usage.setWorkHours(getSumOfTimeEntries(TimeEntry::getWorkHours));
        usage.setEmpHoursUsed(getSumOfTimeEntries(TimeEntry::getSickEmpHours));
        usage.setFamHoursUsed(getSumOfTimeEntries(TimeEntry::getSickFamHours));
        usage.setHolHoursUsed(getSumOfTimeEntries(TimeEntry::getHolidayHours));
        usage.setMiscHoursUsed(getSumOfTimeEntries(TimeEntry::getMiscHours));
        usage.setPerHoursUsed(getSumOfTimeEntries(TimeEntry::getPersonalHours));
        usage.setTravelHoursUsed(getSumOfTimeEntries(TimeEntry::getTravelHours));
        usage.setVacHoursUsed(getSumOfTimeEntries(TimeEntry::getVacationHours));
        return usage;
    }

    public BigDecimal getSumOfTimeEntries(Function<? super TimeEntry, BigDecimal> mapper) {
        return timeEntries.stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** --- Basic Getters/Setters --- */

    public BigInteger getTimeRecordId() {
        return timeRecordId;
    }

    public void setTimeRecordId(BigInteger timeRecordId) {
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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
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

    public LocalDate getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDate processedDate) {
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

    public LocalDateTime getTxOriginalDate() {
        return txOriginalDate;
    }

    public void setTxOriginalDate(LocalDateTime txOriginalDate) {
        this.txOriginalDate = txOriginalDate;
    }

    public LocalDateTime getTxUpdateDate() {
        return txUpdateDate;
    }

    public void setTxUpdateDate(LocalDateTime txUpdateDate) {
        this.txUpdateDate = txUpdateDate;
    }

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntry> timeEntries) {
        this.timeEntries = timeEntries;
    }

    public String getRespHeadCode() {
        return respHeadCode;
    }

    public void setRespHeadCode(String respHeadCode) {
        this.respHeadCode = respHeadCode;
    }
}
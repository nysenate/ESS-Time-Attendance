package gov.nysenate.seta.model.accrual;

import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.personnel.TransactionRecord;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AccrualGap
{
    protected int empId;
    protected Date startDate;
    protected Date endDate;

    protected List<PayPeriod> gapPeriods;
    protected LinkedList<TransactionRecord> recordsDuringGap;
    protected LinkedList<PeriodAccrualUsage> periodUsageRecs;

    public AccrualGap(int empId, Date startDate, Date endDate) {
        this.empId = empId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** --- Functional Getters/Setters --- */

    public LinkedList<TransactionRecord> getTransRecsDuringPeriod(PayPeriod payPeriod) {
        LinkedList<TransactionRecord> recs = new LinkedList<>();
        for (TransactionRecord rec : recordsDuringGap) {
            if (rec.getEffectDate().compareTo(payPeriod.getStartDate()) >= 0 &&
                    rec.getEffectDate().compareTo(payPeriod.getEndDate()) <= 0) {
                recs.add(rec);
            }
            else {
                break;
            }
        }
        return recs;
    }

    public PeriodAccrualUsage getUsageRecDuringPeriod(PayPeriod payPeriod) {
        for (PeriodAccrualUsage rec : periodUsageRecs) {
            if (rec.getPayPeriod().equals(payPeriod)) {
                return rec;
            }
        }
        return null;
    }

    /** --- Basic Getters/Setters --- */

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<PayPeriod> getGapPeriods() {
        return gapPeriods;
    }

    public void setGapPeriods(List<PayPeriod> gapPeriods) {
        this.gapPeriods = gapPeriods;
    }

    public LinkedList<TransactionRecord> getRecordsDuringGap() {
        return recordsDuringGap;
    }

    public void setRecordsDuringGap(LinkedList<TransactionRecord> recordsDuringGap) {
        this.recordsDuringGap = recordsDuringGap;
    }

    public LinkedList<PeriodAccrualUsage> getPeriodUsageRecs() {
        return periodUsageRecs;
    }

    public void setPeriodUsageRecs(LinkedList<PeriodAccrualUsage> periodUsageRecs) {
        this.periodUsageRecs = periodUsageRecs;
    }
}

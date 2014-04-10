package gov.nysenate.seta.model.personnel;

import java.util.Date;
import java.util.Map;

public class TransactionRecord
{
    protected int employeeId;
    protected int changeId;
    protected boolean active;
    protected TransactionType transType;
    protected Map<String, String> valueMap;
    protected Date originalDate;
    protected Date updateDate;
    protected Date effectDate;

    public TransactionRecord() {}

    public boolean hasValues() {
        return valueMap != null && !valueMap.isEmpty();
    }

    /** Basic Getters/Setters */

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getChangeId() {
        return changeId;
    }

    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public TransactionType getTransType() {
        return transType;
    }

    public void setTransType(TransactionType transType) {
        this.transType = transType;
    }

    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, String> valueMap) {
        this.valueMap = valueMap;
    }

    public Date getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(Date originalDate) {
        this.originalDate = originalDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(Date effectDate) {
        this.effectDate = effectDate;
    }
}
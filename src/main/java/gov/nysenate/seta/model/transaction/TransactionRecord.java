package gov.nysenate.seta.model.transaction;

import gov.nysenate.seta.model.transaction.TransactionCode;

import java.util.Date;
import java.util.Map;

public class TransactionRecord
{
    protected int employeeId;
    protected int changeId;
    protected boolean active;
    protected TransactionCode transType;
    protected Map<String, String> valueMap;
    protected Date originalDate;
    protected Date updateDate;
    protected Date effectDate;

    public TransactionRecord() {}

    public boolean hasValues() {
        return valueMap != null && !valueMap.isEmpty();
    }

    /** Functional Getters/Setters */

    /**
     * Delegate to retrieve the value associated with the given column name.
     * @param colName String
     * @return String if value exists, null if it doesn't or is set as null,
     *         or throws IllegalStateException if the value map was not initialized.
     */
    public String getValue(String colName) {
        if (valueMap != null) {
            return valueMap.get(colName);
        }
        throw new IllegalStateException("The value map for the transaction record was not set.");
    }

    /**
     * Checks if the map has a non null value for the given column name.
     * @param colName String
     * @return boolean
     */
    public boolean hasNonNullValue(String colName) {
        if (valueMap != null) {
            return (valueMap.containsKey(colName) && valueMap.get(colName) != null);
        }
        throw new IllegalStateException("The value map for the transaction record was not set.");
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

    public TransactionCode getTransType() {
        return transType;
    }

    public void setTransType(TransactionCode transType) {
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
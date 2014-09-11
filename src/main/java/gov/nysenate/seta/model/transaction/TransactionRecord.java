package gov.nysenate.seta.model.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * A TransactionRecord represents a single unit of change that was made to an
 * employee's personnel or payroll data and is identified by a TransactionCode.
 * A map of the values affected in the database are stored in this record.
 */
public class TransactionRecord
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionRecord.class);

    /** The employee id that this record belongs to. */
    protected int employeeId;

    /** A reference to the change id used for grouping transactions. */
    protected int changeId;

    /** Indicates if record is active or inactive. */
    protected boolean active;

    /** The transaction code indicates the kinds of changes made. */
    protected TransactionCode transCode;

    /** A mapping of the transaction's database column names to their values. */
    protected Map<String, String> valueMap;

    /** The date in which this transaction is effective. */
    protected Date effectDate;

    /** The date when this record was created. */
    protected Date originalDate;

    /** The date when this record was updated. */
    protected Date updateDate;

    public TransactionRecord() {}

    public boolean hasValues() {
        return valueMap != null && !valueMap.isEmpty();
    }

    /** --- Functional Getters/Setters --- */

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

    /** --- Basic Getters/Setters --- */

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

    public TransactionCode getTransCode() {
        return transCode;
    }

    public void setTransCode(TransactionCode transCode) {
        this.transCode = transCode;
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
package gov.nysenate.seta.model;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The TransactionHistory provides an ordered collection of TransactionRecords. This class is intended to be
 * used in methods that need to know about the history of a specific TransactionType for an employee.
 */
public class TransactionHistory
{
    protected int employeeId;
    protected TransactionType transType;
    protected PriorityQueue<TransactionRecord> records;

    public TransactionHistory(int empId, TransactionType type) {
        this.employeeId = empId;
        this.transType = type;
        this.records = new PriorityQueue<>(11, new Comparator<TransactionRecord>() {
            /** Order the records by most recent effect date/origin date */
            @Override
            public int compare(TransactionRecord o1, TransactionRecord o2) {
                int dateCompare = o2.getEffectDate().compareTo(o1.getEffectDate());
                return (dateCompare != 0) ? dateCompare : o2.getOriginalDate().compareTo(o1.originalDate);
            }
        });
    }

    public boolean hasRecords() {
        return !records.isEmpty();
    }

    /** Functional Getters/Setters */

    public void addTransactionRecord(TransactionRecord record) {
        this.records.add(record);
    }

    public void addTransactionRecords(List<TransactionRecord> recordsList) {
        this.records.addAll(recordsList);
    }

    /** Basic Getters/Setters */

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public TransactionType getTransType() {
        return transType;
    }

    public void setTransType(TransactionType transType) {
        this.transType = transType;
    }

    public PriorityQueue<TransactionRecord> getRecords() {
        return this.records;
    }
}

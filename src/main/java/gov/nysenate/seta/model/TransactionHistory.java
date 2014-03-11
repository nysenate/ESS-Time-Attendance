package gov.nysenate.seta.model;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class TransactionHistory
{
    protected int employeeId;
    protected TransactionType transType;
    protected TreeSet<TransactionRecord> records;

    public TransactionHistory() {
        this.records = new TreeSet<>(new Comparator<TransactionRecord>() {
            @Override
            public int compare(TransactionRecord o1, TransactionRecord o2) {
                int dateCompare = o1.getEffectDate().compareTo(o2.getEffectDate());
                return (dateCompare != 0) ? dateCompare : o1.getOriginalDate().compareTo(o2.originalDate);
            }
        });
    }

    /** Functional Getters/Setters */

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

    public TreeSet<TransactionRecord> getRecords() {
        return this.records;
    }
}

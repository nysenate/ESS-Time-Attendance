package gov.nysenate.seta.model.personnel;

import java.util.*;

/**
 * The TransactionHistory provides an ordered collection of TransactionRecords. This class is intended to be
 * used in methods that need to know about the history of a specific TransactionType for an employee.
 */
public class TransactionHistory
{
    protected int employeeId;
    protected Map<TransactionType, PriorityQueue<TransactionRecord>> recordHistory;

    public TransactionHistory(int empId) {
        this.employeeId = empId;
        this.recordHistory = new HashMap<>();
    }

    /**
     * Returns true if any records exist in the history.
     * @return boolean
     */
    public boolean hasRecords() {
        return !recordHistory.isEmpty();
    }

    /**
     * Returns true if records exist for a given type of transaction.
     * @param type TransactionType
     * @return boolean
     */
    public boolean hasRecords(TransactionType type) {
        return recordHistory.get(type) != null && !recordHistory.get(type).isEmpty();
    }

    /**
     * Create a priority queue such that the records are ordered by most recent effect date/origin date first.
     * @return PriorityQueue<TransactionRecord>
     */
    protected PriorityQueue<TransactionRecord> createPriorityQueue() {
        return new PriorityQueue<>(11, new Comparator<TransactionRecord>() {
            @Override
            public int compare(TransactionRecord o1, TransactionRecord o2) {
                int dateCompare = o2.getEffectDate().compareTo(o1.getEffectDate());
                return (dateCompare != 0) ? dateCompare : o2.getOriginalDate().compareTo(o1.originalDate);
            }
        });
    }

    /** Functional Getters/Setters */

    /**
     * Adds a transaction record to the history queue.
     * @param record TransactionRecord
     */
    public void addTransactionRecord(TransactionRecord record) {
        if (record != null) {
            TransactionType type = record.getTransType();
            if (!recordHistory.containsKey(type)) {
                recordHistory.put(type, createPriorityQueue());
            }
            this.recordHistory.get(type).add(record);
        }
    }

    /**
     * Adds a collection of transaction records to their respective history queue.
     * @param recordsList List<TransactionRecord>
     */
    public void addTransactionRecords(List<TransactionRecord> recordsList) {
        for (TransactionRecord record : recordsList) {
            this.addTransactionRecord(record);
        }
    }

    /**
     * Returns a LinkedList containing the ordered TransactionRecords of a specific type.
     * @param type TransactionType
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getTransRecords(TransactionType type) {
        LinkedList<TransactionRecord> recordList = new LinkedList<>();
        if (recordHistory.containsKey(type)) {
           recordList = getListFromPriorityQueue(recordHistory.get(type));
        }
        return recordList;
    }

    /**
     * See overloaded method. This provides the option to change the sort order for the returned list of records.
     * @param type TransactionType
     * @param sortByDateAsc boolean - If true, list will be ordered by earliest effect date first.
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getTransRecords(TransactionType type, boolean sortByDateAsc) {
        LinkedList<TransactionRecord> records = getTransRecords(type);
        if (sortByDateAsc) {
            Collections.reverse(records);
        }
        return records;
    }

    /**
     * See overloaded method. Shorthand method to retrieve every transaction type.
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getAllTransRecords(boolean sortByDateAsc) {
        return getAllTransRecords(recordHistory.keySet(), sortByDateAsc);
    }

    /**
     * Returns a single ordered LinkedList containing all the transaction records. This is useful if
     * you need all the transaction records to be ordered into a single collection.
     * @param transTypes Set<TransactionType> - The set of types to return in the list
     * @param sortByDateAsc boolean - If true, list will be ordered by earliest effect date first.
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getAllTransRecords(Set<TransactionType> transTypes, boolean sortByDateAsc) {
        PriorityQueue<TransactionRecord> allRecords = createPriorityQueue();
        for (TransactionType type : recordHistory.keySet()) {
            if (transTypes.contains(type)) {
                allRecords.addAll(recordHistory.get(type));
            }
        }
        LinkedList<TransactionRecord> records = getListFromPriorityQueue(allRecords);
        if (sortByDateAsc) {
            Collections.reverse(records);
        }
        return records;
    }

    /**
     * Converts a queue containing TransactionRecords into a LinkedList. The supplied queue
     * is not modified in any way.
     * @param queue PriorityQueue<TransactionRecord>
     * @return LinkedList<TransactionRecord>
     */
    private LinkedList<TransactionRecord> getListFromPriorityQueue(final PriorityQueue<TransactionRecord> queue) {
        LinkedList<TransactionRecord> recordList = new LinkedList<>();
        if (queue != null) {
            PriorityQueue<TransactionRecord> copy = new PriorityQueue<>(queue);
            TransactionRecord record = copy.poll();
            while (record != null) {
                recordList.add(record);
                record = copy.poll();
            }
        }
        return recordList;
    }

    /** Basic Getters/Setters */

    public int getEmployeeId() {
        return employeeId;
    }
}

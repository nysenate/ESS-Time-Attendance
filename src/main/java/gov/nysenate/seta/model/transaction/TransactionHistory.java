package gov.nysenate.seta.model.transaction;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import gov.nysenate.seta.dao.base.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The TransactionHistory provides an ordered collection of TransactionRecords. This class is intended to be
 * used in methods that need to know about the history of a specific TransactionCode for an employee.
 */
public class TransactionHistory
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistory.class);

    /** The employee id that this history refers to. */
    protected int employeeId;

    /** A collection of TransactionRecords grouped via the TransactionCode. */
    protected LinkedListMultimap<TransactionCode, TransactionRecord> recordMultimap;

    /** Indicates if the earliest transaction record should represent the initial state, similar to an
     *  APP transaction. This is due to some early employees who are missing APP transactions. */
    protected boolean earliestRecLikeAppoint = false;

    /** --- Constructors --- */

    public TransactionHistory(int empId) {
        this(empId, false);
    }

    public TransactionHistory(int empId, boolean earliestRecLikeAppoint) {
        this.employeeId = empId;
        this.recordMultimap = LinkedListMultimap.create();
        this.earliestRecLikeAppoint = earliestRecLikeAppoint;
    }

    /**
     * Returns true if any records exist in the history.
     * @return boolean
     */
    public boolean hasRecords() {
        return !recordMultimap.isEmpty();
    }

    /**
     * Returns true if records exist for a given transaction code.
     * @param code TransactionCode
     * @return boolean
     */
    public boolean hasRecords(TransactionCode code) {
        return !recordMultimap.get(code).isEmpty();
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Adds a transaction record to the history queue.
     * @param record TransactionRecord
     * @param isFirstRec Indicator we are adding the first record
     */
    public void addTransactionRecord(TransactionRecord record, boolean isFirstRec) {
        if (record != null) {
            TransactionCode code = record.getTransCode();
            if (isFirstRec) {
                code = TransactionCode.APP;
            }
            recordMultimap.put(code, record);
        }
    }

    /**
     * Adds a collection of transaction records to their respective history queue.
     * @param recordsList List<TransactionRecord>
     */
    public void addTransactionRecords(List<TransactionRecord> recordsList) {
        boolean addFirstRecord = true;
        for (TransactionRecord record : recordsList) {
            this.addTransactionRecord(record, addFirstRecord);
            addFirstRecord = false;
        }
    }

    /**
     * See overloaded method.
     * @see #getTransRecords(java.util.Set, gov.nysenate.seta.dao.base.SortOrder)
     * @param code TransactionCode
     * @param dateSort SortOrder
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getTransRecords(TransactionCode code, SortOrder dateSort) {
        return getTransRecords(new HashSet<>(Arrays.asList(code)), dateSort);
    }

    /**
     * Returns a single ordered LinkedList containing a set of transaction records. This is useful if
     * you need a subset of the transaction records to be ordered into a single collection.
     * @param transCodes Set<TransactionCode> - The set of transaction codes to return in the list.
     * @param dateSort SortOrder - Sort order based on the effective date
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getTransRecords(Set<TransactionCode> transCodes, SortOrder dateSort) {
        LinkedList<TransactionRecord> sortedRecList = new LinkedList<>();
        recordMultimap.keySet().stream()
            .filter(transCodes::contains)
            .forEach(code -> sortedRecList.addAll(recordMultimap.get(code)));
        sortedRecList.sort((dateSort.equals(SortOrder.ASC)) ? new TransDateAscending() : new TransDateDescending());
        return sortedRecList;
    }

    /**
     * Shorthand method to retrieve every available transaction record.
     * @see #getTransRecords(java.util.Set, gov.nysenate.seta.dao.base.SortOrder)
     * @param dateOrder SortOrder - Sort order based on the effective date
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getAllTransRecords(SortOrder dateOrder) {
        return getTransRecords(recordMultimap.keySet(), dateOrder);
    }

    /**
     * Get an immutable copy of the record multimap stored in this transaction history.
     * @return ImmutableMultimap<TransactionCode, TransactionRecord>
     */
    public ImmutableMultimap<TransactionCode, TransactionRecord> getRecordMultimap() {
        return ImmutableMultimap.copyOf(recordMultimap);
    }

    /** --- Local classes --- */

    protected static class TransDateAscending implements Comparator<TransactionRecord>
    {
        @Override
        public int compare(TransactionRecord o1, TransactionRecord o2) {
            return ComparisonChain.start()
                .compare(o1.getEffectDate(), o2.getEffectDate())
                .compare(o1.getOriginalDate(), o2.getOriginalDate())
                .result();
        }
    }

    protected static class TransDateDescending implements Comparator<TransactionRecord>
    {
        @Override
        public int compare(TransactionRecord o1, TransactionRecord o2) {
            return ComparisonChain.start()
                .compare(o2.getEffectDate(), o1.getEffectDate())
                .compare(o2.getOriginalDate(), o1.getOriginalDate())
                .result();
        }
    }

    /** --- Basic Getters/Setters --- */

    public int getEmployeeId() {
        return employeeId;
    }
}
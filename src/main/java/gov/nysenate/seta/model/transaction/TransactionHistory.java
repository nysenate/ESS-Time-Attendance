package gov.nysenate.seta.model.transaction;

import com.google.common.collect.*;
import gov.nysenate.seta.dao.base.SortOrder;
import gov.nysenate.seta.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

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
    protected LinkedListMultimap<TransactionCode, TransactionRecord> recordsByCode;

    /** Creates a full snapshot view from the records containing every column and grouped by effective date. */
    protected TreeMap<LocalDate, Map<String, String>> recordSnapshots;

    /** --- Constructors --- */

    public TransactionHistory(int empId, List<TransactionRecord> recordsList) {
        this.employeeId = empId;

        // Sort the input record list from earliest first. (just to be safe)
        List<TransactionRecord> sortedRecs = new ArrayList<>(recordsList);
        sortedRecs.sort(new TransDateAscending());

        // Initialize the data structures
        this.recordsByCode = LinkedListMultimap.create();
        this.recordSnapshots = new TreeMap<>();

        // Store the records
        this.addTransactionRecords(recordsList);
    }

    /**
     * Returns true if any records exist in the history.
     *
     * @return boolean
     */
    public boolean hasRecords() {
        return !recordsByCode.isEmpty();
    }

    /**
     * Returns true if records exist for a given transaction code.
     *
     * @param code TransactionCode
     * @return boolean
     */
    public boolean hasRecords(TransactionCode code) {
        return !recordsByCode.get(code).isEmpty();
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Adds a transaction record to the history queue.
     *
     * @param record TransactionRecord
     */
    private void addTransactionRecord(TransactionRecord record) {
        if (record != null) {
            recordsByCode.put(record.getTransCode(), record);
            LocalDate effectDate = record.getEffectDate();
            if (recordSnapshots.isEmpty()) {
                // Initialize the snapshot map
                recordSnapshots.put(effectDate, record.getValueMap());
            }
            else {
                // Update the previous map with the newly updated values
                Map<String, String> valueMap = Maps.newHashMap(recordSnapshots.lastEntry().getValue());
                valueMap.putAll(record.getValueMap());
                recordSnapshots.put(effectDate, valueMap);
            }
        }
        else {
            throw new IllegalArgumentException("Cannot add a null record to the transaction history!");
        }
    }

    /**
     * Adds a collection of transaction records to their respective history queue.
     *
     * @param recordsList List<TransactionRecord>
     */
    private void addTransactionRecords(List<TransactionRecord> recordsList) {
        if (!recordsList.isEmpty()) {
            recordsList.forEach(this::addTransactionRecord);
        }
    }

    /**
     * See overloaded method.
     *
     * @see #getTransRecords(java.util.Set, gov.nysenate.seta.dao.base.SortOrder)
     * @param code TransactionCode
     * @param dateSort SortOrder
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getTransRecords(TransactionCode code, SortOrder dateSort) {
        return getTransRecords(Sets.newHashSet(code), dateSort);
    }

    /**
     * Returns a single ordered LinkedList containing a set of transaction records. This is useful if
     * you need a subset of the transaction records to be ordered into a single collection.
     *
     * @param transCodes Set<TransactionCode> - The set of transaction codes to return in the list.
     * @param dateSort SortOrder - Sort order based on the effective date
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getTransRecords(Set<TransactionCode> transCodes, SortOrder dateSort) {
        LinkedList<TransactionRecord> sortedRecList = new LinkedList<>();
        recordsByCode.keySet().stream()
            .filter(transCodes::contains)
            .forEach(code -> sortedRecList.addAll(recordsByCode.get(code)));
        sortedRecList.sort((dateSort.equals(SortOrder.ASC)) ? new TransDateAscending() : new TransDateDescending());
        return sortedRecList;
    }

    /**
     * Shorthand method to retrieve every available transaction record.
     *
     * @see #getTransRecords(java.util.Set, gov.nysenate.seta.dao.base.SortOrder)
     * @param dateOrder SortOrder - Sort order based on the effective date
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getAllTransRecords(SortOrder dateOrder) {
        return getTransRecords(recordsByCode.keySet(), dateOrder);
    }

    /**
     * @see #getLatestValueOf(String, boolean)
     * 'latestDate' defaults to a date far in the future.
     */
    public Optional<String> getLatestValueOf(String key, boolean skipNulls) {
        return getLatestValueOf(key, LocalDate.MAX, skipNulls);
    }

    /**
     * Given the 'key' find the most recent value associated with it in the values map.
     *
     * @param key String - A key from the values map (e.g. 'NALAST')
     * @param latestDate LocalDate - The latest date to search until.
     * @param skipNulls boolean - Set to true if you want to find the latest non-null value.
     * @return Optional<String> - If the value is found, it will be set, otherwise an empty Optional is returned.
     */
    public Optional<String> getLatestValueOf(String key, LocalDate latestDate, boolean skipNulls) {
        return this.recordSnapshots.headMap(latestDate, true)
            .descendingMap().entrySet().stream()
            .filter(e -> (!skipNulls || e.getValue().get(key) != null)) // Skip null values if requested
            .map(e -> e.getValue().get(key))                            // Extract the value for the given 'key'
            .findFirst();                                               // Return most recent one
    }

    /**
     * @see #getEarliestValueOf(String, LocalDate, boolean)
     * 'earliestDate' defaults to a date way in the past.
     */
    public Optional<String> getEarliestValueOf(String key, boolean skipNulls) {
       return getEarliestValueOf(key, LocalDate.ofYearDay(1, 1), skipNulls);
    }

    /**
     * Given the 'key' find the earliest value associated with it in the values map.
     *
     * @param key String - A key from the value map (e.g. 'NALAST').
     * @param earliestDate LocalDate - The earliest date for which to search from.
     * @param skipNulls boolean - Set to true if you want to find the earliest non-null value.
     * @return Optional<String> - If the value is found, it will be set, otherwise an empty Optional is returned.
     */
    public Optional<String> getEarliestValueOf(String key, LocalDate earliestDate, boolean skipNulls) {
        return this.recordSnapshots.tailMap(earliestDate, true)
            .entrySet().stream()
            .filter(e -> (!skipNulls || e.getValue().get(key) != null)) // Skip null values if requested
            .map(e -> e.getValue().get(key))                            // Extract the value for the given 'key'
            .findFirst();                                               // Return most recent one
    }

    /**
     * Get an immutable copy of the record multimap stored in this transaction history.
     *
     * @return ImmutableMultimap<TransactionCode, TransactionRecord>
     */
    public ImmutableMultimap<TransactionCode, TransactionRecord> getRecordsByCode() {
        return ImmutableMultimap.copyOf(recordsByCode);
    }

    /**
     * Get an immutable copy of the record snapshot map stored in this transaction history.
     *
     * @return ImmutableMap<LocalDate, Map<String, String>>
     */
    public ImmutableMap<LocalDate, Map<String, String>> getRecordSnapshots() {
        return ImmutableMap.copyOf(recordSnapshots);
    }

    /** --- Local classes --- */

    /** Sort by earliest (effective date, origin date) first. */
    protected static class TransDateAscending implements Comparator<TransactionRecord>
    {
        @Override
        public int compare(TransactionRecord o1, TransactionRecord o2) {
            return ComparisonChain.start()
                .compare(o1.getEffectDate(), o2.getEffectDate())
                .compare(o1.getOriginalDate(), o2.getOriginalDate())
                .compare(o1.getTransCode().name(), o2.getTransCode().name())
                .result();
        }
    }

    /** Sort by most recent (effective date, origin date) first. */
    protected static class TransDateDescending implements Comparator<TransactionRecord>
    {
        @Override
        public int compare(TransactionRecord o1, TransactionRecord o2) {
            return ComparisonChain.start()
                .compare(o2.getEffectDate(), o1.getEffectDate())
                .compare(o2.getOriginalDate(), o1.getOriginalDate())
                .compare(o1.getTransCode().name(), o2.getTransCode().name())
                .result();
        }
    }

    /** --- Basic Getters/Setters --- */

    public int getEmployeeId() {
        return employeeId;
    }
}
package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.model.exception.TransRecordException;
import gov.nysenate.seta.model.personnel.TransactionHistory;
import gov.nysenate.seta.model.personnel.TransactionRecord;
import gov.nysenate.seta.model.personnel.TransactionType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * The transactions referred to in this interface are the actions performed by Personnel and Payroll staff on
 * a particular employee's record. Since an employee's information will change over time (e.g their salary, or T&A
 * Supervisor) this interface helps to obtain a collection of the transactions that have taken place so that a
 * point in time representation can be re-created for an employee.
 */
public interface EmployeeTransactionDao
{
    /** See overloaded method. {@code start} defaults to the beginning of time and {@code end} is today.
     * @see #getLastTransRecord(int, gov.nysenate.seta.model.personnel.TransactionType, java.util.Date, java.util.Date)
     */
    public TransactionRecord getLastTransRecord(int empId, TransactionType type) throws TransRecordException;

    /**
     * See overloaded method. {@code start} defaults to the beginning of time.
     * @see #getLastTransRecord(int, gov.nysenate.seta.model.personnel.TransactionType, java.util.Date, java.util.Date)
     */
    public TransactionRecord getLastTransRecord(int empId, TransactionType type, Date end) throws TransRecordException;

    /**
     * Retrieves the most recent TransactionRecord of the given type with an effective date between the 'start'
     * and 'end' date.
     * @param empId int - Employee id
     * @param type TransactionRecord - the type of transaction to search for
     * @param start Date - the transaction's effective date must be equal to or after this date.
     * @param end Date - the transaction's effective date must be equal to or before this date.
     * @return TransactionRecord if exists, otherwise a TransRecordException is thrown
     * @throws TransRecordException - TransRecordNotFoundEx if given transaction does not exist for empId.
     */
    public TransactionRecord getLastTransRecord(int empId, TransactionType type, Date start, Date end) throws TransRecordException;

    /**
     * See overloaded method. {@code start} defaults to the beginning of time and {@code end} is today.
     * @see #getLastTransRecords(int, java.util.Set, java.util.Date, java.util.Date)
     */
    public Map<TransactionType, TransactionRecord> getLastTransRecords(int empId, Set<TransactionType> types);

    /**
     * See overloaded method. {@code start} defaults to the beginning of time.
     * @see #getLastTransRecords(int, java.util.Set, java.util.Date, java.util.Date)
     */
    public Map<TransactionType, TransactionRecord> getLastTransRecords(int empId, Set<TransactionType> types, Date end);

    /**
     * Retrieves a map (TransactionType -> TransactionRecord) of the most recent transaction for each type in the
     * given set with an effective date between the 'start' and 'end' date. If a TransactionType does not exist for the
     * employee the map will not contain a key-value pair for it. An exception will not be thrown in this case.
     * @param empId int - Employee id
     * @param types Set<TransactionType> - the set of transactions to retrieve
     * @param start Date - the transaction's effective date must be equal to or after this date.
     * @param end Date - the transaction's effective date must be equal to or before this date.
     * @return Map(TransactionType,TransactionRecord) - only found transaction types will be set in the map.
     */
    public Map<TransactionType, TransactionRecord> getLastTransRecords(int empId, Set<TransactionType> types, Date start, Date end);

    /**
     * See overloaded method. {@code start} defaults to the beginning of time and {@code end} is today.
     * @see #getTransHistory(int, java.util.Set, java.util.Date, java.util.Date)
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionType> types);

    /**
     * See overloaded method. {@code start} defaults to the beginning of time.
     * @see #getTransHistory(int, java.util.Set, java.util.Date, java.util.Date)
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionType> types, Date end);

    /**
     * Retrieves a TransactionHistory of all the records for the given set of TransactionTypes that have an
     * effective date before or equal to the 'end' date.
     *
     * @param empId int - Employee id
     * @param types TransactionType - the set of transactions to retrieve
     * @param start Date - the transaction's effective date must be equal to or after this date.
     * @param end Date - the transaction's effective date must be equal to or before this date.
     * @return TransactionHistory
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionType> types, Date start, Date end);
}
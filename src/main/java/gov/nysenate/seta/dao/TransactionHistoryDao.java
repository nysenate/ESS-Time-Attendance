package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TransRecordException;
import gov.nysenate.seta.model.TransactionHistory;
import gov.nysenate.seta.model.TransactionRecord;
import gov.nysenate.seta.model.TransactionType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * The transactions referred to in this interface are the actions performed by Personnel and Payroll staff on
 * a particular employee's record. Since an employee's information will change over time (e.g their salary, or T&A
 * Supervisor) this interface helps to obtain a collection of the transactions that have taken place so that a
 * point in time representation can be re-created for an employee.
 */
public interface TransactionHistoryDao
{
    /**
     * Retrieves the most recent TransactionRecord of the given type with an effective date before or equal to today's date.
     * @param empId int - Employee id
     * @param type TransactionRecord - the type of transaction to search for
     * @return TransactionRecord if exists, otherwise a TransRecordException is thrown
     * @throws TransRecordException - TransRecordNotFoundEx if given transaction does not exist for empId.
     */
    public TransactionRecord getLastTransactionRecord(int empId, TransactionType type) throws TransRecordException;

    /**
     * Retrieves the most recent TransactionRecord of the given type with an effective date before or equal to the 'end' date.
     * @param empId int - Employee id
     * @param type TransactionRecord - the type of transaction to search for
     * @param end Date - the transaction will be restricted to the 'end' date
     * @return TransactionRecord if exists, otherwise a TransRecordException is thrown
     * @throws TransRecordException - TransRecordNotFoundEx if given transaction does not exist for empId.
     */
    public TransactionRecord getLastTransactionRecord(int empId, TransactionType type, Date end) throws TransRecordException;

    /**
     * Retrieves a map (TransactionType -> TransactionRecord) of the most recent transaction for each type in the
     * given set with an effective date before or equal to today's date. If a TransactionType does not exist for the
     * employee the map will not contain a key-value pair for it. An exception will not be thrown in this case.
     * @param empId int - Employee id
     * @param types  Set<TransactionType> - the set of transactions to retrieve
     * @return Map(TransactionType,TransactionRecord) - only found transaction types will be set in the map.
     */
    public Map<TransactionType, TransactionRecord> getLastTransactionRecords(int empId, Set<TransactionType> types);

    /**
     * Retrieves a map (TransactionType -> TransactionRecord) of the most recent transaction for each type in the
     * given set with an effective date before or equal to 'end' date. If a TransactionType does not exist for the
     * employee the map will not contain a key-value pair for it. An exception will not be thrown in this case.
     * @param empId int - Employee id
     * @param types Set<TransactionType> - the set of transactions to retrieve
     * @param end Date - the transaction will be restricted to the 'end' date
     * @return Map(TransactionType,TransactionRecord) - only found transaction types will be set in the map.
     */
    public Map<TransactionType, TransactionRecord> getLastTransactionRecords(int empId, Set<TransactionType> types, Date end);

    /**
     * Retrieves TransactionHistory for the given employee id and transaction type. If no records are matched
     * a TransRecordException will be thrown.
     * @param empId int - Employee id
     * @param type TransactionType - the type of transaction to search for
     * @return TransactionHistory if records exist, otherwise a TransRecordException is thrown
     * @throws TransRecordException - TransRecordNotFoundEx if given transaction does not exist for empId.
     */
    public TransactionHistory getTransactionHistory(int empId, TransactionType type) throws TransRecordException;

    /**
     * Retrieves TransactionHistory for the given employee id and transaction type with an effective date that is
     * before or equal to the 'end' date. If no records are matched a TransRecordException will be thrown.
     * @param empId int - Employee id
     * @param type TransactionType - the type of transaction to search for
     * @return TransactionHistory if records exist, otherwise a TransRecordException is thrown
     * @throws TransRecordException - TransRecordNotFoundEx if given transaction does not exist for empId.
     */
    public TransactionHistory getTransactionHistory(int empId, TransactionType type, Date end) throws TransRecordException;

    /**
     * Retrieves a map (TransactionType -> TransactionHistory) of all the records for the given set of TransactionTypes.
     * If no records are found, the map will be empty, i.e an error will not be thrown.
     * @param empId int - Employee id
     * @param types TransactionType - the set of transactions to retrieve
     * @return Map(TransactionType, TransactionHistory)
     */
    public Map<TransactionType, TransactionHistory> getTransactionHistoryMap(int empId, Set<TransactionType> types);

    /**
     * Retrieves a map (TransactionType -> TransactionHistory) of all the records for the given set of TransactionTypes
     * that have an effective date before or equal to the 'end' date. If no records are found, the map will be empty,
     * i.e an error will not be thrown.
     * @param empId int - Employee id
     * @param types TransactionType - the set of transactions to retrieve
     * @param end the transaction will be restricted to the 'end' date
     * @return Map(TransactionType, TransactionHistory)
     */
    public Map<TransactionType, TransactionHistory> getTransactionHistoryMap(int empId, Set<TransactionType> types, Date end);
}
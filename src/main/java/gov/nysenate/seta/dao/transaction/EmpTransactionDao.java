package gov.nysenate.seta.dao.transaction;

import com.google.common.collect.Range;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;

import java.time.LocalDate;
import java.util.Set;

/**
 * The transactions referred to in this interface are the actions performed by Personnel and Payroll staff on
 * a particular employee's record. Since an employee's information will change over time (e.g their salary, or T&A
 * Supervisor) this interface helps to obtain a collection of the transactions that have taken place so that a
 * point in time representation can be re-created for an employee.
 */
public interface EmpTransactionDao
{
    /** --- Transaction Retrieval --- */

    /**
     * Gets all transactions for the given emp id.
     * @see #getTransHistory(int, java.util.Set, EmpTransDaoOption)
     */
    public TransactionHistory getTransHistory(int empId, EmpTransDaoOption options);

    /**
     * Gets just the transactions specified in the 'codes' set for the given emp id.*
     * @see #getTransHistory(int, java.util.Set, com.google.common.collect.Range, EmpTransDaoOption)
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, EmpTransDaoOption options);

    /**
     * Retrieves a TransactionHistory of all the records for the given set of TransactionCodes that have an
     * effective date that fits within the given dateRange.
     *
     * @param empId      int              Employee id
     * @param codes      TransactionCode  The set of transactions to retrieve.
     * @param dateRange  Range<LocalDate> Fetch only the transactions that have an effective date that
     *                                    fits within this range.
     * @param options    TransDaoOption   Options for retrieving the transaction.
     * @return TransactionHistory
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Range<LocalDate> dateRange,
                                              EmpTransDaoOption options);
}
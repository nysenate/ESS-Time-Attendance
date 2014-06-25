package gov.nysenate.seta.dao.transaction;

import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionCode;

import java.util.Date;
import java.util.Set;

/**
 * The transactions referred to in this interface are the actions performed by Personnel and Payroll staff on
 * a particular employee's record. Since an employee's information will change over time (e.g their salary, or T&A
 * Supervisor) this interface helps to obtain a collection of the transactions that have taken place so that a
 * point in time representation can be re-created for an employee.
 */
public interface EmployeeTransactionDao {
    /**
     * See overloaded method. {@code start} defaults to the beginning of time and {@code end} is today.
     *
     * //@see #getTransHistory(int, java.util.Set, java.util.Date, java.util.Date)
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes);

    /**
     * See overloaded method. {@code start} defaults to the beginning of time and {@code end} is today.
     *
     * //@see #getTransHistory(int, java.util.Set, java.util.Date, java.util.Date)
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, boolean earliestRecLikeAppoint);

    /**
     * See overloaded method. {@code start} defaults to the beginning of time.
     *
     * //@see #getTransHistory(int, java.util.Set, java.util.Date, java.util.Date)
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date end);


    /**
     * See overloaded method. {@code start} defaults to the beginning of time.
     *
     * //@see #getTransHistory(int, java.util.Set, java.util.Date, java.util.Date)
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date end, boolean earliestRecLikeAppoint);

    /**
     * See overloaded method. {@code start} defaults to the beginning of time.
     *
     * @see #getTransHistory(int, java.util.Set, java.util.Date, java.util.Date, boolean)
     */

    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end);

    /**
     * Retrieves a TransactionHistory of all the records for the given set of TransactionCodes that have an
     * effective date before or equal to the 'end' date.
     *
     * @param empId int - Employee id
     * @param codes TransactionCode - the set of transactions to retrieve
     * @param start Date - the transaction's effective date must be equal to or after this date.
     * @param end   Date - the transaction's effective date must be equal to or before this date.
     * @return TransactionHistory
     */
    //public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end, boolean earliestRecLikeAppoint);

    /**
     * Retrieves a TransactionHistory of all the records for the given set of TransactionCodes that have an
     * effective date before or equal to the 'end' date.
     *
     * @param empId int - Employee id
     * @param codes TransactionCode - the set of transactions to retrieve
     * @param start Date - the transaction's effective date must be equal to or after this date.
     * @param end   Date - the transaction's effective date must be equal to or before this date.
     * @param earliestRecLikeAppoint    boolean - Whether or not to treat the first record (regardless of thr transaction codes)
     *                                  like an appoint/reappoint where all columns are initialized.
     * @return TransactionHistory
     */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end, boolean earliestRecLikeAppoint);
}

package gov.nysenate.seta.service.transaction;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;

import java.time.LocalDate;
import java.util.Set;

public interface TransactionService {

    /**
     * Gets all transactions for the given employee as they exist in the data store
     * @see #getTransHistory(int, EmpTransDaoOption, Set, Range)
     */
    TransactionHistory getTransHistory(int empId);

    /**
     * Gets all transactions for the given emp id.  May modify the first transaction according to the options param
     * @see #getTransHistory(int, EmpTransDaoOption, Set, Range)
     */
    TransactionHistory getTransHistory(int empId, EmpTransDaoOption options);

    /**
     * Gets just the transactions specified in the 'codes' set for the given emp id.*
     * @see #getTransHistory(int, EmpTransDaoOption, Set)
     */
    TransactionHistory getTransHistory(int empId, EmpTransDaoOption options, Set<TransactionCode> codes);

    /**
     * Retrieves a TransactionHistory of all the records for the given set of TransactionCodes that have an
     * effective date that fits within the given dateRange.
     *
     * @param empId      int              Employee id
     * @param options    TransDaoOption   Options for retrieving the transaction.
     * @param codes      TransactionCode  The set of transactions to retrieve.
     * @param dateRange  Range<LocalDate> Fetch only the transactions that have an effective date that
     *                                    fits within this range.
     * @return TransactionHistory
     */
    TransactionHistory getTransHistory(int empId, EmpTransDaoOption options,
                                       Set<TransactionCode> codes, Range<LocalDate> dateRange);
}

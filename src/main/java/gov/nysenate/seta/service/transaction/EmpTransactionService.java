package gov.nysenate.seta.service.transaction;

import gov.nysenate.seta.model.transaction.TransactionHistory;

public interface EmpTransactionService
{
    /**
     * Gets all transactions for the given emp id.
     * First rec will be an appointment rec.
     */
    TransactionHistory getTransHistory(int empId);

    /**
     * Gets all transactions for the given emp id.
     */
//    TransactionHistory getTransHistory(int empId, boolean requireAppRec);
}
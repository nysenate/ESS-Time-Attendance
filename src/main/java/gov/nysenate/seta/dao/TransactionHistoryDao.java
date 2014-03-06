package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TransactionRecord;
import gov.nysenate.seta.model.TransactionType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TransactionHistoryDao
{
    public TransactionRecord getLastTransactionRecord(int empId, TransactionType type);

    public TransactionRecord getLastTransactionRecord(int empId, TransactionType type, Date end);

    public Map<TransactionType, TransactionRecord> getLastTransactionRecords(int empId, Set<TransactionType> types);

    public Map<TransactionType, TransactionRecord> getLastTransactionRecords(int empId, Set<TransactionType> types, Date end);
}

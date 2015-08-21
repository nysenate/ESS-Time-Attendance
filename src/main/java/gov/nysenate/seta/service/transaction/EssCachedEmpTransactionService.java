package gov.nysenate.seta.service.transaction;

import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.dao.transaction.EmpTransactionDao;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionHistoryMissingEx;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.service.base.BaseCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import static gov.nysenate.seta.model.transaction.TransactionCode.APP;
import static org.springframework.cache.Cache.ValueWrapper;

@Service
public class EssCachedEmpTransactionService extends BaseCachingService<Integer> implements EmpTransactionService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmpTransactionService.class);

    @Autowired EmpTransactionDao transactionDao;

    /** --- Transaction Service Methods --- */

    /** {@inheritDoc} */
//    @Override
//    public TransactionHistory getTransHistory(int empId) {
//         return getTransHistory(empId, true);
//    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId) {
        try {
            ValueWrapper cacheResult = null;
            if (isCacheReady()) {
                cacheResult = primaryCache.get(empId);
            }
            TransactionHistory transHistory = cacheResult != null ? (TransactionHistory) cacheResult.get() : null;
            if (transHistory == null) {
                transHistory = transactionDao.getTransHistory(empId, EmpTransDaoOption.INITIALIZE_AS_APP);
                if (isCacheReady()) {
                    primaryCache.put(empId, transHistory);
                }
            }
            return transHistory;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new TransactionHistoryMissingEx(empId);
        }
    }

    protected void initializeAppointmentRec(TransactionHistory history) {
        TransactionRecord first = history.getAllTransRecords(SortOrder.ASC).getFirst();
        if (!first.getTransCode().isAppointType()) {
            first.setTransCode(APP);
        }
    }

    /** --- Caching Service Methods --- */

    @Override
    public ContentCache getCacheType() {
        return ContentCache.TRANSACTION;
    }

    @Override
    public void warmCaches() {
        preCacheWarm();
        evictCaches();
        //todo warm caches
        postCacheWarm();
    }
}

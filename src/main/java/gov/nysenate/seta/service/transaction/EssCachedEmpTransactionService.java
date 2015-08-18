package gov.nysenate.seta.service.transaction;

import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.dao.transaction.EmpTransactionDao;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.service.base.BaseCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static gov.nysenate.seta.model.transaction.TransactionCode.APP;
import static gov.nysenate.seta.model.transaction.TransactionCode.RTP;
import static org.springframework.cache.Cache.ValueWrapper;

@Service
public class EssCachedEmpTransactionService extends BaseCachingService<Integer> implements EmpTransactionService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmpTransactionService.class);

    @Autowired EmpTransactionDao transactionDao;

    /** --- Transaction Service Methods --- */

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId) throws EmployeeNotFoundEx {
        try {
            ValueWrapper cacheResult = null;
            if (isCacheReady()) {
                cacheResult = primaryCache.get(empId);
            }
            TransactionHistory transHistory = cacheResult != null ? (TransactionHistory) cacheResult.get() : null;
            if (transHistory == null) {
                transHistory = transactionDao.getTransHistory(empId, EmpTransDaoOption.DEFAULT);
                if (isCacheReady()) {
                    primaryCache.put(empId, transHistory);
                }
            }
            return transHistory;
        } catch (EmptyResultDataAccessException ex) {
            throw new EmployeeNotFoundEx(empId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, EmpTransDaoOption options) {
        TransactionHistory transHistory = getTransHistory(empId);
        if (options.shouldSetToApp() && transHistory.hasRecords()) {
            // Ensure the first record is APP / RTP if the options dictate it
            TransactionRecord firstRecord = transHistory.getAllTransRecords(SortOrder.ASC).getFirst();
            if (firstRecord.getTransCode() != APP && firstRecord.getTransCode() != RTP) {
                firstRecord.setTransCode(APP);
            }
        }
        return transHistory;
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, EmpTransDaoOption options, Set<TransactionCode> codes) {
        return getTransHistory(empId, options, codes, Range.all());
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, EmpTransDaoOption options,
                                              Set<TransactionCode> codes, Range<LocalDate> dateRange) {
        TransactionHistory transHistory = getTransHistory(empId, options);
        Iterator<TransactionRecord> it = transHistory.getAllTransRecords(SortOrder.ASC).iterator();
        List<TransactionRecord> filteredRecords = new LinkedList<>();
        // Add the first record if the options request it
        if (options.shouldInitialize() && it.hasNext()) {
            filteredRecords.add(it.next());
        }
        // Add the remaining records
        it.forEachRemaining(transRecord -> {
            if (codes.contains(transRecord.getTransCode()) && dateRange.contains(transRecord.getEffectDate())) {
                filteredRecords.add(transRecord);
            }
        });
        return new TransactionHistory(empId, filteredRecords);
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

package gov.nysenate.seta.service.transaction;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.cache.CacheEvictIdEvent;
import gov.nysenate.seta.model.cache.ContentCache;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class CachedTransactionServiceTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(CachedTransactionServiceTests.class);

    @Autowired CachedTransactionService transService;

    @Autowired EventBus eventBus;

    private void timedGet(int empId) {
        Stopwatch sw = Stopwatch.createStarted();
        transService.getTransHistory(empId);
        logger.info("got trans history for {}:  {}", empId, sw.stop().elapsed(TimeUnit.NANOSECONDS));
    }

    @Test
    public void cacheTest() {
        int empId = 11423;
        for (int i = 0; i < 4; i++) {
            timedGet(empId);
        }
    }

    @Test
    public void invalidateTest() {
        int empId = 11423;
        cacheTest();
        logger.info("invalidating {}!", empId);
        eventBus.post(new CacheEvictIdEvent<>(ContentCache.TRANSACTION, empId));
        cacheTest();
    }
}

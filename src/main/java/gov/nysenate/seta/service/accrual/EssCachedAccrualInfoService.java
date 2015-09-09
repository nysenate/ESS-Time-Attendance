package gov.nysenate.seta.service.accrual;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.accrual.AccrualDao;
import gov.nysenate.seta.model.accrual.AnnualAccSummary;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.service.cache.EhCacheManageService;
import gov.nysenate.seta.service.period.PayPeriodService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Service
public class EssCachedAccrualInfoService implements AccrualInfoService
{
    @Autowired private AccrualDao accrualDao;
    @Autowired private PayPeriodService payPeriodService;

    @Autowired private EhCacheManageService cacheManageService;
    @Autowired private EventBus eventBus;

    private Cache annualAccrualCache;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        setupCaches();
    }

    public void setupCaches() {
        this.annualAccrualCache = cacheManageService.registerEternalCache(ContentCache.ACCRUAL_ANNUAL.name());
    }

    private static final class AnnualAccCacheTree {
        TreeMap<Integer, AnnualAccSummary> annualAccruals;
        public AnnualAccCacheTree(TreeMap<Integer, AnnualAccSummary> annualAccruals) {
            this.annualAccruals = annualAccruals;
        }
        public TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int endYear) {
            return new TreeMap<>(annualAccruals.headMap(endYear, true));
        }
    }

    @Override
    public TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear) {
        annualAccrualCache.acquireReadLockOnKey(empId);
        Element elem = annualAccrualCache.get(empId);
        annualAccrualCache.releaseReadLockOnKey(empId);
        AnnualAccCacheTree cachedAccTree;
        if (elem == null) {
            TreeMap<Integer, AnnualAccSummary> annualAccruals =
                accrualDao.getAnnualAccruals(empId, DateUtils.THE_FUTURE.getYear());
            cachedAccTree = new AnnualAccCacheTree(annualAccruals);
            putAnnualAccTreeInCache(empId, cachedAccTree);
        }
        else {
            cachedAccTree = (AnnualAccCacheTree) elem.getObjectValue();
        }
        return cachedAccTree.getAnnualAccruals(endYear);
    }

    private void putAnnualAccTreeInCache(int empId, AnnualAccCacheTree annualAccCacheTree) {
        annualAccrualCache.acquireWriteLockOnKey(empId);
        annualAccrualCache.put(new Element(empId, annualAccCacheTree));
        annualAccrualCache.releaseWriteLockOnKey(empId);
    }

    @Override
    public List<PayPeriod> getActiveAttendancePeriods(int empId, LocalDate endDate, SortOrder dateOrder) {
        TreeMap<Integer, AnnualAccSummary> annAcc = getAnnualAccruals(empId, endDate.getYear());
        Optional<Integer> openYear = annAcc.descendingMap().entrySet().stream()
                .filter(e -> e.getValue().getCloseDate() == null)
                .map(Map.Entry::getKey)
                .findFirst();
        if (openYear.isPresent()) {
            return payPeriodService.getPayPeriods(
                PayPeriodType.AF, Range.closed(LocalDate.of(openYear.get(), 1, 1), endDate), dateOrder);
        }
        return new ArrayList<>();
    }
}
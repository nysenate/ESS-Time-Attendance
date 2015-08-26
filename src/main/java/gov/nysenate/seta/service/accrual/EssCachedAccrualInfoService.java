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
import gov.nysenate.seta.service.period.PayPeriodService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
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

    @Autowired private CacheManager cacheManager;
    @Autowired private EventBus eventBus;

    protected EhCacheCache primaryCache;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        setupCaches();
    }

    public void setupCaches() {
        Cache cache = new Cache(new CacheConfiguration()
            .name(ContentCache.ACCRUAL_ANNUAL.name())
            .eternal(true));
        cacheManager.addCache(cache);
        this.primaryCache = new EhCacheCache(cache);
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
        ValueWrapper cachedAccTreeWrapper = this.primaryCache.get(empId);
        AnnualAccCacheTree cachedAccTree;
        if (cachedAccTreeWrapper == null) {
            TreeMap<Integer, AnnualAccSummary> annualAccruals =
                accrualDao.getAnnualAccruals(empId, DateUtils.THE_FUTURE.getYear());
            cachedAccTree = new AnnualAccCacheTree(annualAccruals);
            this.primaryCache.put(empId, new AnnualAccCacheTree(annualAccruals));
        }
        else {
            cachedAccTree = (AnnualAccCacheTree) cachedAccTreeWrapper.get();
        }
        return cachedAccTree.getAnnualAccruals(endYear);
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
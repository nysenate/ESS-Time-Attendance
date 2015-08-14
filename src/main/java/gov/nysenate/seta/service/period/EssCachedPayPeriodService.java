package gov.nysenate.seta.service.period;

import com.google.common.collect.*;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.exception.PayPeriodNotFoundEx;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.service.base.BaseCachingService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class EssCachedPayPeriodService extends BaseCachingService<PayPeriod> implements PayPeriodService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedPayPeriodService.class);

    @Autowired private PayPeriodDao payPeriodDao;

    @PostConstruct
    public void init() {
        super.init();
        warmCaches();
    }

    /**
     * Since we lookup pay periods by determining if the given date intersects the period's date range,
     * we need to store the periods in a tree map to avoid having to loop through each one to find the
     * one we want.
     */
    private static class PayPeriodCacheTree
    {
        private final RangeMap<LocalDate, PayPeriod> rangeMap = TreeRangeMap.create();

        public PayPeriodCacheTree(TreeSet<PayPeriod> periodSet) {
            periodSet.forEach(p -> {
                rangeMap.put(Range.closed(p.getStartDate(), p.getEndDate()), p);
            });
        }

        public PayPeriod getPayPeriod(LocalDate date) {
            PayPeriod period = rangeMap.get(date);
            if (period == null) throw new PayPeriodNotFoundEx("Pay period containing date " + date + " could not be found.");
            return period;
        }

        public List<PayPeriod> getPayPeriodsInRange(Range<LocalDate> dateRange, SortOrder dateOrder) {
            List<PayPeriod> payPeriods = new ArrayList<>(rangeMap.subRangeMap(dateRange).asMapOfRanges().values());
            if (dateOrder.equals(SortOrder.DESC)) {
                Collections.reverse(payPeriods);
            }
            return payPeriods;
        }
    }

    @Override
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date) throws PayPeriodNotFoundEx {
        if (this.primaryCache.get(type) != null) {
            PayPeriodCacheTree payPeriodCacheTree = (PayPeriodCacheTree) this.primaryCache.get(type).get();
            return payPeriodCacheTree.getPayPeriod(date);
        }
        return payPeriodDao.getPayPeriod(type, date);
    }

    @Override
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder) {
        if (this.primaryCache.get(type) != null) {
            PayPeriodCacheTree payPeriodCacheTree = (PayPeriodCacheTree) this.primaryCache.get(type).get();
            return payPeriodCacheTree.getPayPeriodsInRange(dateRange, dateOrder);
        }
        return payPeriodDao.getPayPeriods(type, dateRange, dateOrder);
    }

    @Override
    public ContentCache getCacheType() {
        return ContentCache.PAY_PERIOD;
    }

    @Override
    public void warmCaches() {
        logger.debug("Fetching all AF pay period recs for caching...");
        Range<LocalDate> cacheRange = Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED);
        TreeSet<PayPeriod> payPeriods =
            new TreeSet<>(payPeriodDao.getPayPeriods(PayPeriodType.AF, cacheRange, SortOrder.ASC));
        this.primaryCache.put(PayPeriodType.AF, new PayPeriodCacheTree(payPeriods));
        logger.info("Done caching {} AF pay period records.", payPeriods.size());
    }
}

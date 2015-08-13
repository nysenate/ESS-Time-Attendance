package gov.nysenate.seta.service.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.service.base.BaseCachingService;
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

    private static class PayPeriodCacheTree
    {
        private final TreeMap<LocalDate, PayPeriod> startDateMap = new TreeMap<>();

        public PayPeriodCacheTree(TreeSet<PayPeriod> periodSet) {
            periodSet.forEach(p -> {
                startDateMap.put(p.getStartDate(), p);
            });
        }

        public List<PayPeriod> getPayPeriodsInRange(Range<LocalDate> dateRange, SortOrder dateOrder) {
            LocalDate fromDate = DateUtils.startOfDateRange(dateRange);
            LocalDate toDate = DateUtils.endOfDateRange(dateRange);
            ArrayList<PayPeriod> payPeriods = new ArrayList<>(startDateMap.subMap(fromDate, true, toDate, true).values());
            if (!payPeriods.isEmpty()) {
                // Need to account for when the range includes a date that begins in the middle of a pay period.
                if (payPeriods.get(0).getStartDate().compareTo(fromDate) > 0) {
                    payPeriods.add(0, startDateMap.floorEntry(fromDate).getValue());
                }
                if (dateOrder.equals(SortOrder.DESC)) {
                    Collections.reverse(payPeriods);
                }
            }
            return payPeriods;
        }
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

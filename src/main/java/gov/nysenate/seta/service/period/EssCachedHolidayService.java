package gov.nysenate.seta.service.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.period.HolidayDao;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.exception.HolidayNotFoundForDateEx;
import gov.nysenate.seta.model.payroll.Holiday;
import gov.nysenate.seta.service.base.BaseCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EssCachedHolidayService extends BaseCachingService<Holiday> implements HolidayService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedHolidayService.class);
    private static final String HOLIDAY_CACHE_KEY = "holiday";

    private static final class HolidayCacheTree
    {
        private TreeMap<LocalDate, Holiday> holidayTreeMap = new TreeMap<>();

        public HolidayCacheTree(List<Holiday> holidays) {
            holidays.forEach(h -> holidayTreeMap.put(h.getDate(), h));
        }

        public Optional<Holiday> getHoliday(LocalDate date) {
            return Optional.ofNullable(holidayTreeMap.get(date));
        }

        public List<Holiday> getHolidays(Range<LocalDate> dateRange) {
            return new ArrayList<>(holidayTreeMap.subMap(DateUtils.startOfDateRange(dateRange), true,
                                         DateUtils.endOfDateRange(dateRange), true).values());
        }
    }

    @Autowired private HolidayDao holidayDao;

    @PostConstruct
    public void init() {
        super.init();
        warmCaches();
    }

    @Override
    public Optional<Holiday> getHoliday(LocalDate date) {
        if (isCacheReady() && this.primaryCache.get(HOLIDAY_CACHE_KEY) != null) {
            HolidayCacheTree cacheTree = (HolidayCacheTree) this.primaryCache.get(HOLIDAY_CACHE_KEY).get();
            return cacheTree.getHoliday(date);
        }
        try {
            Holiday holiday = holidayDao.getHoliday(date);
            return Optional.of(holiday);
        }
        catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, boolean includeQuestionable, SortOrder dateOrder) {
        if (isCacheReady() && this.primaryCache.get(HOLIDAY_CACHE_KEY) != null) {
            HolidayCacheTree cacheTree = (HolidayCacheTree) this.primaryCache.get(HOLIDAY_CACHE_KEY).get();
            List<Holiday> holidays = cacheTree.getHolidays(dateRange);
            if (!includeQuestionable) {
                holidays = holidays.stream().filter(h -> !h.isQuestionable()).collect(Collectors.toList());
            }
            if (dateOrder.equals(SortOrder.DESC)) {
                Collections.reverse(holidays);
            }
            return holidays;
        }
        return holidayDao.getHolidays(dateRange, includeQuestionable, dateOrder);
    }

    @Override
    public ContentCache getCacheType() {
        return ContentCache.HOLIDAY;
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // Refresh once a day
    public void warmCaches() {
        preCacheWarm();
        evictCaches();
        logger.info("Caching holidays...");
        this.primaryCache.put(HOLIDAY_CACHE_KEY, new HolidayCacheTree(
            holidayDao.getHolidays(Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED), true, SortOrder.ASC)));
        logger.info("Done caching holidays.");
        postCacheWarm();
    }
}

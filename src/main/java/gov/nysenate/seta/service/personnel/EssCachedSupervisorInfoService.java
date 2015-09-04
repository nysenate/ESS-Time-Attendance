package gov.nysenate.seta.service.personnel;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.EmployeeSupInfo;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import gov.nysenate.seta.service.cache.EhCacheManageService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EssCachedSupervisorInfoService implements SupervisorInfoService
{
    @Autowired private SupervisorDao supervisorDao;
    @Autowired private EhCacheManageService cacheManageService;
    @Autowired private EventBus eventBus;

    private Cache supEmployeeGroupCache;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        supEmployeeGroupCache = cacheManageService.registerTimeBasedCache(ContentCache.SUPERVISOR_EMP_GROUP.name(), 3600L);
    }

    @Override
    public boolean isSupervisorDuring(Range<LocalDate> dateRange) {
        return false;
    }

    @Override
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException {
        SupervisorEmpGroup empGroup;
        supEmployeeGroupCache.acquireReadLockOnKey(supId);
        Element cachedElem = supEmployeeGroupCache.get(supId);
        supEmployeeGroupCache.releaseReadLockOnKey(supId);
        if (cachedElem == null) {
            empGroup = supervisorDao.getSupervisorEmpGroup(supId, DateUtils.ALL_DATES);
            putSupEmpGroupInCache(empGroup);
        }
        else {
            empGroup = (SupervisorEmpGroup) cachedElem.getObjectValue();
        }
        SupervisorEmpGroup filteredEmpGroup = new SupervisorEmpGroup(empGroup);
        filteredEmpGroup.setStartDate(DateUtils.startOfDateRange(dateRange));
        filteredEmpGroup.setEndDate(DateUtils.endOfDateRange(dateRange));
        filteredEmpGroup.filterActiveEmployeesByDate(dateRange);
        return filteredEmpGroup;
    }

    private void putSupEmpGroupInCache(SupervisorEmpGroup empGroup) {
        supEmployeeGroupCache.acquireWriteLockOnKey(empGroup.getSupervisorId());
        try {
            supEmployeeGroupCache.put(new Element(empGroup.getSupervisorId(), empGroup));
        }
        finally {
            supEmployeeGroupCache.releaseWriteLockOnKey(empGroup.getSupervisorId());
        }
    }
}
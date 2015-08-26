package gov.nysenate.seta.service.personnel;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.base.BaseCachingService;
import gov.nysenate.seta.service.base.CachingService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class EssCachedEmployeeInfoService extends BaseCachingService<Employee>
                                          implements EmployeeInfoService, CachingService<Employee>
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmployeeInfoService.class);

    @Autowired protected EmployeeDao employeeDao;
    @Autowired protected EmpTransactionService transService;

    @Override
    public Employee getEmployee(int empId) throws EmployeeNotFoundEx {
        if (isCacheReady()) {
            Cache.ValueWrapper cacheValue = primaryCache.get(empId);
            if (cacheValue != null) {
                return (Employee) cacheValue.get();
            }
        }
        Employee employee = employeeDao.getEmployeeById(empId);
        if (isCacheReady()) {
            this.primaryCache.put(empId, employee);
        }
        return employee;
    }

    @Override
    public RangeSet<LocalDate> getEmployeeActiveDatesService(int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        RangeSet<LocalDate> activeDates = TreeRangeSet.create();
        LocalDate currActive = null;
        for (Map.Entry<LocalDate, Boolean> status : transHistory.getEffectiveEmpStatus(DateUtils.ALL_DATES).entrySet()) {
            if (status.getValue()) {
                currActive = status.getKey();
            }
            else if (currActive != null) {
                activeDates.add(Range.closed(currActive, status.getKey()));
                currActive = null;
            }
        }
        if (currActive != null) {
            activeDates.add(Range.atLeast(currActive));
        }
        return activeDates;
    }

    @Override
    public ContentCache getCacheType() {
        return ContentCache.EMPLOYEE;
    }

    @Override
    public void warmCaches() {
        preCacheWarm();
        postCacheWarm();
    }
}
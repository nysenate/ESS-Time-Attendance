package gov.nysenate.seta.service.personnel;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.eventbus.EventBus;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.cache.EhCacheManageService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;

@Service
public class EssCachedEmployeeInfoService implements EmployeeInfoService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmployeeInfoService.class);

    @Autowired protected EmployeeDao employeeDao;
    @Autowired protected EmpTransactionService transService;
    @Autowired protected EventBus eventBus;
    @Autowired protected EhCacheManageService cacheManageService;

    protected volatile Cache empCache;

    @PostConstruct
    protected void init() {
        this.eventBus.register(this);
        this.empCache = this.cacheManageService.registerEternalCache("employees");
    }

    @Override
    public Employee getEmployee(int empId) throws EmployeeNotFoundEx {
        empCache.acquireReadLockOnKey(empId);
        Element elem = empCache.get(empId);
        empCache.releaseReadLockOnKey(empId);
        if (elem != null) {
            return (Employee) elem.getObjectValue();
        }
        else {
            Employee employee = employeeDao.getEmployeeById(empId);
            fixFullNameFormat(employee);
            empCache.acquireWriteLockOnKey(empId);
            empCache.put(new Element(empId, employee));
            empCache.releaseWriteLockOnKey(empId);
            return employee;
        }
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

    private void fixFullNameFormat(Employee employee) {
        String fullName =
            employee.getFirstName() + " " +
            ((StringUtils.isNotBlank(employee.getInitial())) ? (employee.getInitial() + " ") : "") +
            employee.getLastName() + " " +
            ((StringUtils.isNotBlank(employee.getSuffix())) ? employee.getSuffix() : "");
        employee.setFullName(WordUtils.capitalizeFully(fullName.toLowerCase()).trim().replaceAll("\\s+", " "));
    }
}
package gov.nysenate.seta.service.personnel;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.eventbus.EventBus;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.RangeUtils;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.personnel.*;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.cache.EhCacheManageService;
import gov.nysenate.seta.model.unit.Address;
import gov.nysenate.seta.model.unit.Location;
import gov.nysenate.seta.service.base.BaseCachingService;
import gov.nysenate.seta.service.base.CachingService;
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
    public Employee getEmployee(int empId, LocalDate effectiveDate) throws EmployeeNotFoundEx {
        Employee employee = getEmployee(empId);
        TransactionHistory transHistory = transService.getTransHistory(empId);
        employee.setActive(transHistory.latestValueOf("CDEMPSTATUS", effectiveDate, true).orElse("I").equals("A"));
        employee.setSupervisorId(
                Integer.parseInt(transHistory.latestValueOf("NUXREFSV", effectiveDate, true).orElse("0")));
        employee.setJobTitle(transHistory.latestValueOf("CDEMPTITLE", effectiveDate, false).orElse(null));
        try {
            employee.setPayType(PayType.valueOf(transHistory.latestValueOf("CDPAYTYPE", effectiveDate, true).orElse(null)));
        } catch (NullPointerException | IllegalArgumentException ignored) {}
        employee.setRespCenter(getRespCenterAtDate(transHistory, effectiveDate));
        employee.setWorkLocation(getWorkLocAtDate(transHistory, effectiveDate));
        return employee;
    }

    @Override
    public RangeSet<LocalDate> getEmployeeActiveDatesService(int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        RangeSet<LocalDate> employedDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(transHistory.getEffectiveEmpStatus(DateUtils.ALL_DATES), DateUtils.THE_FUTURE)
                .asMapOfRanges().forEach((range, employed) -> {
            if (employed) {
                employedDates.add(range);
            }
        });
        return employedDates;
    }

    private void fixFullNameFormat(Employee employee) {
        String fullName =
            employee.getFirstName() + " " +
            ((StringUtils.isNotBlank(employee.getInitial())) ? (employee.getInitial() + " ") : "") +
            employee.getLastName() + " " +
            ((StringUtils.isNotBlank(employee.getSuffix())) ? employee.getSuffix() : "");
        employee.setFullName(WordUtils.capitalizeFully(fullName.toLowerCase()).trim().replaceAll("\\s+", " "));
    }

    /** --- Internal Methods --- */

    /**
     * These methods extract the most up to date value of a particular employee field from a transaction history
     * effective after a given date
     * TODO
     *  you can't get every value of these objects from the transaction layer
     */
    private static ResponsibilityCenter getRespCenterAtDate(TransactionHistory transHistory, LocalDate effectiveDate) {
        ResponsibilityCenter rctr = new ResponsibilityCenter();
        rctr.setCode(Integer.parseInt(transHistory.latestValueOf("CDRESPCTR", effectiveDate, true).orElse("0")));
        rctr.setAgency(getAgencyAtDate(transHistory, effectiveDate));
        rctr.setHead(getRespHeadAtDate(transHistory, effectiveDate));
        return rctr;
    }
    
    private static Agency getAgencyAtDate(TransactionHistory transHistory, LocalDate effectiveDate) {
        Agency agency = new Agency();
        agency.setCode(transHistory.latestValueOf("CDAGENCY", effectiveDate, true).orElse(null));
        return agency;
    }
    
    private static ResponsibilityHead getRespHeadAtDate(TransactionHistory transHistory, LocalDate effectiveDate) {
        ResponsibilityHead rHead = new ResponsibilityHead();
        rHead.setCode(transHistory.latestValueOf("CDRESPCTRHD", effectiveDate, true).orElse(null));
        return rHead;
    }
    
    private static Location getWorkLocAtDate(TransactionHistory transHistory, LocalDate effectiveDate) {
        Location loc = new Location();
        loc.setCode(transHistory.latestValueOf("CDLOCAT", effectiveDate, true).orElse(null));
        return loc;
    }
}
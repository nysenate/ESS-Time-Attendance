package gov.nysenate.seta.service.personnel;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.RangeUtils;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.personnel.*;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.seta.model.unit.Location;
import gov.nysenate.seta.service.cache.EhCacheManageService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Service
public class EssCachedEmployeeInfoService implements EmployeeInfoService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmployeeInfoService.class);

    @Autowired protected Environment env;
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

    /** {@inheritDoc} */
    @Override
    public Employee getEmployee(int empId) throws EmployeeNotFoundEx {
        empCache.acquireReadLockOnKey(empId);
        Element elem = empCache.get(empId);
        empCache.releaseReadLockOnKey(empId);
        if (elem != null) {
            return (Employee) elem.getObjectValue();
        }
        else {
            return getEmployeeAndPutInCache(empId);
        }
    }

    /** {@inheritDoc} */
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
        setRespCenterAtDate(employee, transHistory, effectiveDate);
        getWorkLocAtDate(employee, transHistory, effectiveDate);
        return employee;
    }

    /** {@inheritDoc} */
    @Override
    public RangeSet<LocalDate> getEmployeeActiveDatesService(int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        RangeSet<LocalDate> employedDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(transHistory.getEffectiveEmpStatus(DateUtils.ALL_DATES))
                .asMapOfRanges().forEach((range, employed) -> {
            if (employed) {
                employedDates.add(range);
            }
        });
        return employedDates;
    }

    /** --- Caching Methods --- */

    /**
     * Fetches the employee from the database with the given empId and saves the Employee object
     * into the employee cache.
     * @param empId int - Employee Id
     * @return Employee
     */
    private Employee getEmployeeAndPutInCache(int empId) {
        Employee employee = employeeDao.getEmployeeById(empId);
        fixFullNameFormat(employee);
        empCache.acquireWriteLockOnKey(empId);
        empCache.put(new Element(empId, employee));
        empCache.releaseWriteLockOnKey(empId);
        return employee;
    }

    @Scheduled(fixedDelayString = "${cache.poll.delay.employees:43200000}")
    public void cacheActiveEmployees() {
        if (env.acceptsProfiles("!test")) {
            logger.debug("Refreshing employee cache..");
            empCache.removeAll();
            employeeDao.getActiveEmployeeIds(LocalDate.now())
                    .parallelStream().forEach((empId) -> {
                logger.debug("Fetching employee {}", empId);
                getEmployeeAndPutInCache(empId);
            });
            logger.debug("Finished refreshing employee cache");
        }
    }

    /** --- Formatting Methods --- */

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
     *
     * These methods extract the most up to date value of a particular employee field from a transaction history
     * effective after a given date
     * TODO: you can't get every value of these objects from the transaction layer
     */
    private static void setRespCenterAtDate(Employee emp, TransactionHistory transHistory, LocalDate effectiveDate) {
        if (emp.getRespCenter() == null) {
            emp.setRespCenter(new ResponsibilityCenter());
        }
        ResponsibilityCenter rctr = emp.getRespCenter();
        rctr.setCode(Integer.parseInt(transHistory.latestValueOf("CDRESPCTR", effectiveDate, true).orElse(Integer.toString(rctr.getCode()))));
        setAgencyAtDate(rctr, transHistory, effectiveDate);
        getRespHeadAtDate(rctr, transHistory, effectiveDate);
    }

    private static void setAgencyAtDate(ResponsibilityCenter respCtr, TransactionHistory transHistory, LocalDate effectiveDate) {
        if (respCtr.getAgency() == null) {
            respCtr.setAgency(new Agency());
        }
        Agency agency = respCtr.getAgency();
        agency.setCode(transHistory.latestValueOf("CDAGENCY", effectiveDate, true).orElse(agency.getCode()));
    }

    private static void getRespHeadAtDate(ResponsibilityCenter respCtr, TransactionHistory transHistory, LocalDate effectiveDate) {
        if (respCtr.getHead() == null) {
            respCtr.setHead(new ResponsibilityHead());
        }
        ResponsibilityHead rHead = respCtr.getHead();
        rHead.setCode(transHistory.latestValueOf("CDRESPCTRHD", effectiveDate, false).orElse(rHead.getCode()));
    }

    private static void getWorkLocAtDate(Employee emp, TransactionHistory transHistory, LocalDate effectiveDate) {
        if (emp.getWorkLocation() == null) {
            emp.setWorkLocation(new Location());
        }
        Location loc = emp.getWorkLocation();
        loc.setCode(transHistory.latestValueOf("CDLOCAT", effectiveDate, true).orElse(loc.getCode()));
    }
}
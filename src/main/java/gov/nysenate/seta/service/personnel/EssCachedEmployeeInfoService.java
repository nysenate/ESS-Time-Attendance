package gov.nysenate.seta.service.personnel;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.RangeUtils;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.cache.ContentCache;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.personnel.*;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.unit.Address;
import gov.nysenate.seta.model.unit.Location;
import gov.nysenate.seta.service.base.BaseCachingService;
import gov.nysenate.seta.service.base.CachingService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    @Override
    public ContentCache getCacheType() {
        return ContentCache.EMPLOYEE;
    }

    @Override
    public void warmCaches() {
        preCacheWarm();
        postCacheWarm();
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
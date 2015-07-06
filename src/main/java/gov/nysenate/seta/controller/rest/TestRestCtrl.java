package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.accrual.AccrualDao;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.exception.PayPeriodException;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeException;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
public class TestRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TestRestCtrl.class);

    @Autowired private SupervisorDao supervisorDao;
    @Autowired private EmployeeDao employeeDao;
    @Autowired private AccrualDao accrualDao;
    @Autowired private PayPeriodDao payPeriodDao;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMddyyyy");

    @RequestMapping(value = "/rest/v1/supEmps/{empId}/{startDateStr}/{endDateStr}",
                   method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public SupervisorEmpGroup viewRecordEntryInfo(@PathVariable Integer empId, @PathVariable String startDateStr,
                                 @PathVariable String endDateStr) {

        LocalDate startDate = LocalDate.from(dtf.parse(startDateStr));
        LocalDate endDate = LocalDate.from(dtf.parse(endDateStr));
        logger.info("Supplied dates: " + startDate + " " + endDate);

        try {
            return supervisorDao.getSupervisorEmpGroup(empId, Range.closed(startDate, endDate));
        }
        catch (SupervisorException e) {
            logger.error("Failed to ", e);
        }

        return null;
    }

    @RequestMapping(value = "/api/v1/emp/{empId}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public Object getEmployee(@PathVariable Integer empId) {
        try {
            return employeeDao.getEmployeeById(empId);
        } catch (EmployeeException e) {
            logger.error("Failed to ", e);
        }
        return null;
    }

    @RequestMapping(BaseRestCtrl.REST_PATH + "/accruals/summaries/period/{payPeriodStr}/emp/{empId}")
    public Object getAccrualSummaries(@PathVariable String payPeriodStr, @PathVariable Integer empId) {
        LocalDate payPeriodStart = parseISODate(payPeriodStr, "pay period");
        try {
            PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, payPeriodStart);
            return accrualDao.getPeriodAccrualSummaries(empId, 2015, payPeriod.getEndDate());
        }
        catch (PayPeriodException e) {
            logger.error("Failed to find pay period starting on {}", payPeriodStart);
        }
//        catch (AccrualException e) {
//            logger.error("Failed to obtain accruals for employee {} for pay period {}", payPeriodStart, empId, e);
//        }
        return null;
    }

    @Autowired AccrualRestCtrl accrualRestCtrl;

    @RequestMapping(BaseRestCtrl.REST_PATH + "/accruals/annual/{endYear}/emp/{empId}")
    public Object getAccrualAnnual(@PathVariable Integer endYear, @PathVariable Integer empId) {
        return accrualDao.getAnnualAccrualSummaries(empId, endYear);
    }


}
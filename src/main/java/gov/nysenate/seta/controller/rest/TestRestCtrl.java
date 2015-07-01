package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.exception.SupervisorException;
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

    @Autowired
    SupervisorDao supervisorDao;

    @Autowired
    EmployeeDao employeeDao;

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

    @RequestMapping(value = "/rest/v1/emp/{empId}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public Employee getEmployee(@PathVariable Integer empId) {
        try {
            return employeeDao.getEmployeeById(empId);
        } catch (EmployeeException e) {
            logger.error("Failed to ", e);
        }
        return null;
    }
}
package gov.nysenate.seta.controller.rest;

import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.personnel.EmployeeException;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.SupervisorChain;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TestRestCtrl.class);

    @Autowired
    SupervisorDao supervisorDao;

    @Autowired
    EmployeeDao employeeDao;

    DateTimeFormatter dtf = DateTimeFormat.forPattern("MMddyyyy");

    @RequestMapping(value = "/rest/v1/supEmps/{empId}/{startDateStr}/{endDateStr}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public @ResponseBody
    SupervisorEmpGroup viewRecordEntryInfo(@PathVariable Integer empId, @PathVariable String startDateStr,
                                 @PathVariable String endDateStr) {

        DateTime startDate = dtf.parseDateTime(startDateStr);
        DateTime endDate = dtf.parseDateTime(endDateStr);
        logger.info("Supplied dates: " + startDate + " " + endDate);

        try {
            return supervisorDao.getSupervisorEmpGroup(empId, startDate.toDate(), endDate.toDate());
        } catch (SupervisorException e) {
            logger.error("Failed to ", e);
        }

        return null;
    }

    @RequestMapping(value = "/rest/v1/supChain/{empId}/{startDateStr}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public @ResponseBody
    SupervisorChain viewSupChain(@PathVariable Integer empId, @PathVariable String startDateStr) {
        DateTime startDate = dtf.parseDateTime(startDateStr);
        try {
            return supervisorDao.getSupervisorChain(empId, startDate.toDate());
        } catch (SupervisorException e) {
            logger.error("Failed to ", e);
        }
        return null;
    }

    @RequestMapping(value = "/rest/v1/emp/{empId}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public @ResponseBody
    Employee getEmployee(@PathVariable Integer empId) {
        try {
            return employeeDao.getEmployeeById(empId);
        } catch (EmployeeException e) {
            logger.error("Failed to ", e);
        }
        return null;
    }

}

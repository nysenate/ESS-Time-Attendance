package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.accrual.AccrualDao;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.exception.PayPeriodException;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
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
}
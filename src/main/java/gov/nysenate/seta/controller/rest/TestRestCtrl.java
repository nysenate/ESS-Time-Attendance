package gov.nysenate.seta.controller.rest;

import gov.nysenate.seta.dao.accrual.AccrualDao;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TestRestCtrl.class);

    @Autowired private SupervisorDao supervisorDao;
    @Autowired private EmployeeDao employeeDao;
    @Autowired private AccrualDao accrualDao;
    @Autowired private PayPeriodDao payPeriodDao;
}
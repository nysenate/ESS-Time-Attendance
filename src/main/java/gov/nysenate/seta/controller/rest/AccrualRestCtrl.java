package gov.nysenate.seta.controller.rest;

import gov.nysenate.seta.client.view.AccrualsView;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.exception.PayPeriodException;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.service.accrual.AccrualService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;

@RestController
@RequestMapping(REST_PATH + "/accruals")
public class AccrualRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AccrualRestCtrl.class);

    @Autowired private AccrualService accrualService;
    @Autowired private PayPeriodDao payPeriodDao;

//    @RequestMapping("/period/{payPeriodStr}/emp/{empId}")
    public Object getAccruals(@PathVariable String payPeriodStr, @PathVariable Integer empId) {
        LocalDate payPeriodStart = parseISODate(payPeriodStr, "pay period");
        try {
            PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, payPeriodStart);
            PeriodAccSummary periodAccSummary = accrualService.getAccruals(empId, payPeriod);
            return periodAccSummary.getSickRate();
        }
        catch (PayPeriodException e) {
            logger.error("Failed to find pay period starting on {}", payPeriodStart);
        }
        catch (AccrualException e) {
            logger.error("Failed to obtain accruals for employee {} for pay period {}", payPeriodStart, empId, e);
        }
        return null;
    }
}

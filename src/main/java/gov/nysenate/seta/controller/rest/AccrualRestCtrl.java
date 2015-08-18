package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.response.error.ErrorCode;
import gov.nysenate.seta.client.response.error.ErrorResponse;
import gov.nysenate.seta.client.view.AccrualsView;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.exception.PayPeriodException;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.service.accrual.AccrualComputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;

@RestController
@RequestMapping(REST_PATH + "/accruals")
public class AccrualRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AccrualRestCtrl.class);

    @Autowired private AccrualComputeService accrualService;
    @Autowired private PayPeriodDao payPeriodDao;

    @RequestMapping("")
    public BaseResponse getAccruals(@RequestParam int empId, @RequestParam String beforeDate) {
        LocalDate beforeLocalDate = parseISODate(beforeDate, "pay period");
        try {
            PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, beforeLocalDate.minusDays(1));
            PeriodAccSummary periodAccSummary = accrualService.getAccruals(empId, payPeriod);
            return new ViewObjectResponse<>(new AccrualsView(periodAccSummary));
        }
        catch (PayPeriodException e) {
            logger.error("Failed to find pay period before {}", beforeLocalDate, e);
        }
        catch (AccrualException e) {
            logger.error("Failed to obtain accruals for employee {}", empId, e);
        }
        return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
    }

    @RequestMapping("/history")
    public BaseResponse getAccruals(@RequestParam int empId, @RequestParam String fromDate, @RequestParam String toDate) {
        LocalDate fromLocalDate = parseISODate(fromDate, "from date");
        LocalDate toLocalDate = parseISODate(toDate, "to date");
        try {
            List<PayPeriod> periods =
                payPeriodDao.getPayPeriods(PayPeriodType.AF, Range.closed(fromLocalDate, toLocalDate), SortOrder.ASC);
            TreeMap<PayPeriod, PeriodAccSummary> accruals = accrualService.getAccruals(empId, periods);
            return ListViewResponse.of(accruals.values().stream().map(AccrualsView::new).collect(Collectors.toList()));
        }
        catch (AccrualException e) {
            logger.error("Failed to obtain accruals for employee {}", empId, e);
        }
        return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
    }
}

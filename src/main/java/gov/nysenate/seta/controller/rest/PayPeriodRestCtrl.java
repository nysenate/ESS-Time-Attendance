package gov.nysenate.seta.controller.rest;

import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.period.PayPeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;

@RestController
@RequestMapping(REST_PATH + "/periods/")
public class PayPeriodRestCtrl extends BaseRestCtrl
{
    @Autowired private PayPeriodDao payPeriodDao;

    @RequestMapping("/{periodTypeStr}/{fromDateStr}/{toDateStr}")
    public BaseResponse getPayPeriods(@PathVariable String fromDateStr, @PathVariable String toDateStr,
                                      @PathVariable String periodTypeStr) {
        LocalDate fromDate = parseISODate(fromDateStr, "from-date");
        LocalDate toDate = parseISODate(toDateStr, "to-date");
        PayPeriodType periodType = PayPeriodType.valueOf(periodTypeStr);

        return null;
    }
}

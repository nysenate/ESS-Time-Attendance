package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.common.LimitOffset;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.view.PayPeriodView;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;

@RestController
@RequestMapping(REST_PATH + "/periods")
public class PayPeriodRestCtrl extends BaseRestCtrl
{
    @Autowired private PayPeriodDao payPeriodDao;

    @RequestMapping("/{periodTypeStr}")
    public BaseResponse getPayPeriods(@PathVariable String periodTypeStr,
                                      @RequestParam(required = false) Integer year,
                                      @RequestParam(required = false) String fromDate,
                                      @RequestParam(required = false) String toDate) {
        List<PayPeriod> payPeriods;
        if (year != null) {
            payPeriods = getPayPeriodList(periodTypeStr, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        }
        else if (fromDate != null) {
            if (toDate == null) toDate = fromDate;
            payPeriods = getPayPeriodList(periodTypeStr, fromDate, toDate);
        }
        else {
            payPeriods = getPayPeriodList(periodTypeStr, LocalDate.ofYearDay(LocalDate.now().getYear(), 1), LocalDate.now());
        }
        return ListViewResponse.of(payPeriods.stream().map(PayPeriodView::new).collect(Collectors.toList()), "periods");
    }

    private List<PayPeriod> getPayPeriodList(String periodTypeStr, String fromDateStr, String toDateStr) {
        LocalDate fromDate = parseISODate(fromDateStr, "from-date");
        LocalDate toDate = parseISODate(toDateStr, "to-date");
        return getPayPeriodList(periodTypeStr, fromDate, toDate);
    }

    private List<PayPeriod> getPayPeriodList(String periodTypeStr, LocalDate fromDate, LocalDate toDate) {
        PayPeriodType periodType = PayPeriodType.valueOf(periodTypeStr);
        return payPeriodDao.getPayPeriods(periodType, Range.closed(fromDate, toDate), SortOrder.ASC);
    }
}
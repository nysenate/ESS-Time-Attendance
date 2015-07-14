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

    @RequestMapping("/{periodTypeStr}/year/{year}")
    public BaseResponse getPayPeriodByYear(@PathVariable String periodTypeStr, @PathVariable Integer year) {
        List<PayPeriod> payPeriods = getPayPeriodList(periodTypeStr, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        return ListViewResponse.of(payPeriods.stream().map(PayPeriodView::new).collect(Collectors.toList()),
                payPeriods.size(), new LimitOffset(payPeriods.size()));
    }

    @RequestMapping("/{periodTypeStr}/date/{dateStr}")
    public BaseResponse getPayPeriod(@PathVariable String periodTypeStr, @PathVariable String dateStr) {
        List<PayPeriod> payPeriods = getPayPeriodList(periodTypeStr, dateStr, dateStr);
        return new ViewObjectResponse<>(new PayPeriodView(payPeriods.get(0)));
    }

    @RequestMapping("/{periodTypeStr}/dates/{fromDateStr}/{toDateStr}")
    public BaseResponse getPayPeriods(@PathVariable String periodTypeStr, @PathVariable String fromDateStr,
                                      @PathVariable String toDateStr) {
        List<PayPeriod> payPeriods = getPayPeriodList(periodTypeStr, fromDateStr, toDateStr);
        return ListViewResponse.of(payPeriods.stream().map(PayPeriodView::new).collect(Collectors.toList()),
                payPeriods.size(), new LimitOffset(payPeriods.size()));
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
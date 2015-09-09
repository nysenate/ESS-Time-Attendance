package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.view.HolidayView;
import gov.nysenate.seta.model.payroll.Holiday;
import gov.nysenate.seta.service.period.HolidayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(REST_PATH + "/holidays")
public class HolidayRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(HolidayRestCtrl.class);

    @Autowired private HolidayService holidayService;

    @RequestMapping(value = "", params = "year")
    public BaseResponse getHolidaysByYear(@RequestParam Integer year, WebRequest request) {
        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDate toDate = LocalDate.of(year, 12, 31);
        return getListViewResponse(getHolidaysDuring(fromDate, toDate, request));
    }

    @RequestMapping(value = "", params = {"fromDate", "toDate"})
    public BaseResponse getHolidays(@RequestParam String fromDate, @RequestParam String toDate, WebRequest request) {
        LocalDate fromLocalDate = parseISODate(fromDate, "from-date");
        LocalDate toLocalDate = parseISODate(toDate, "to-date");
        return getListViewResponse(getHolidaysDuring(fromLocalDate, toLocalDate, request));
    }

    private List<Holiday> getHolidaysDuring(LocalDate fromDate, LocalDate toDate, WebRequest request) {
        return holidayService.getHolidays(Range.closed(fromDate, toDate), true, SortOrder.ASC);
    }

    private BaseResponse getListViewResponse(List<Holiday> holidays) {
        return ListViewResponse.of(
                holidays.stream().map(HolidayView::new).collect(toList()), "holidays");
    }
}
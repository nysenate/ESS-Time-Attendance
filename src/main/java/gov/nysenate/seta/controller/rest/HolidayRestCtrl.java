package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.common.LimitOffset;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.view.HolidayView;
import gov.nysenate.seta.dao.personnel.HolidayDao;
import gov.nysenate.seta.model.payroll.Holiday;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Autowired private HolidayDao holidayDao;

    @RequestMapping("/year/{year}" )
    public BaseResponse getHolidays(@PathVariable Integer year, WebRequest request) {
        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDate toDate = LocalDate.of(year, 12, 31);
        return getListViewResponse(getHolidaysDuring(fromDate, toDate, request));
    }

    @RequestMapping("/dates/{fromDateStr}/{toDateStr}")
    public BaseResponse getHolidays(@PathVariable String fromDateStr, @PathVariable String toDateStr, WebRequest request) {
        LocalDate fromDate = parseISODate(fromDateStr, "from-date");
        LocalDate toDate = parseISODate(toDateStr, "to-date");
        return getListViewResponse(getHolidaysDuring(fromDate, toDate, request));
    }

    private List<Holiday> getHolidaysDuring(LocalDate fromDate, LocalDate toDate, WebRequest request) {
        return holidayDao.getHolidays(Range.closed(fromDate, toDate), true, SortOrder.ASC);
    }

    private BaseResponse getListViewResponse(List<Holiday> holidays) {
        return ListViewResponse.of(
                holidays.stream().map(HolidayView::new).collect(toList()), holidays.size(), new LimitOffset(holidays.size()));
    }
}
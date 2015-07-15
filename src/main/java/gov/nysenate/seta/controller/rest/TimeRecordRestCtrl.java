package gov.nysenate.seta.controller.rest;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.view.TimeRecordView;
import gov.nysenate.seta.client.view.base.ListView;
import gov.nysenate.seta.client.view.base.MapView;
import gov.nysenate.seta.dao.attendance.TimeRecordDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.*;

@RestController
@RequestMapping(REST_PATH + "/records")
public class TimeRecordRestCtrl extends BaseRestCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordRestCtrl.class);

    @Qualifier("remoteTimeRecordDao")
    @Autowired
    private TimeRecordDao timeRecordDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getRecords(@RequestParam Integer[] empId,
                                   @RequestParam String from,
                                   @RequestParam(required = false) String to,
                                   @RequestParam(required = false) String[] status) {
        Set<Integer> empIds = new HashSet<>(Arrays.asList(empId));
        LocalDate toDate = to != null ? parseISODate(to, "to") : LocalDate.now();
        LocalDate fromDate = parseISODate(from, "from");
        Range<LocalDate> dateRange = getClosedRange(fromDate, toDate, "from", "to");
        Set<TimeRecordStatus> statuses;
        if (status.length > 0) {
            statuses = Arrays.asList(status).stream()
                    .map(recordStatus -> getEnumParameter("status", recordStatus, TimeRecordStatus.class))
                    .collect(Collectors.toSet());
        } else {
            statuses = EnumSet.allOf(TimeRecordStatus.class);
        }

        ListMultimap<Integer, TimeRecord> records = timeRecordDao.getRecordsDuring(empIds, dateRange, statuses);

        return new ViewObjectResponse<>(MapView.of(
                records.asMap().values().stream()
                        .map(recordList -> ListView.of(recordList.stream()
                                .map(TimeRecordView::new)
                                .collect(Collectors.toList())))
                        .collect(Collectors.toMap((recordList) -> recordList.items.get(0).getEmployeeId(), Function.identity()))
        ));
    }
}

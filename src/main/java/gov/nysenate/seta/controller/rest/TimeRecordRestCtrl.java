package gov.nysenate.seta.controller.rest;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.view.TimeRecordView;
import gov.nysenate.seta.client.view.base.ListView;
import gov.nysenate.seta.client.view.base.MapView;
import gov.nysenate.seta.dao.attendance.TimeRecordDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.service.attendance.TimeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(REST_PATH + "/records")
public class TimeRecordRestCtrl extends BaseRestCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordRestCtrl.class);

    @Autowired
    private TimeRecordService timeRecordService;

    @Autowired
    private TimeRecordDao timeRecordDao;

    @RequestMapping(value = "/active", produces = APPLICATION_JSON_VALUE)
    public BaseResponse getActiveRecordsJson(@RequestParam Integer[] empId, @RequestParam(required = false) String to) throws Exception {
        if (empId.length == 1) {
            LocalDate endLocalDate = (to != null) ? parseISODate(to, "to") : LocalDate.now();
//            List<TimeRecord> activeRecords = timeRecordService.getTimeRecords(empId[0], endLocalDate);
//            return ListViewResponse.of(activeRecords.stream().map(TimeRecordView::new).collect(toList()));
        }
        throw new UnsupportedOperationException();

    }

    /**
     * Get Time Record API
     * -------------------
     *
     * Get xml or json time records for one or more employees:
     *      (GET) /api/v1/time/records[.json]
     *
     * Request Parameters: empId - int[] - required - Records will be retrieved for these employee ids
     *                     from - Date - required - Gets time records that begin on or after this date
     *                     to - Date - default current date - Gets time records that end before or on this date
     *                     status - String[] - default all statuses - Will only get time records with one of these statuses
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public BaseResponse getRecordsJson(@RequestParam Integer[] empId,
                                       @RequestParam String from,
                                       @RequestParam(required = false) String to,
                                       @RequestParam(required = false) String[] status) {
        return getRecordResponse(getRecords(empId, from, to, status), false);
    }

    /**
     * Save Time Record API
     * --------------------
     *
     * Save a time record:
     *      (POST) /api/v1/records
     *
     * Post Data: json TimeRecordView
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public void saveRecord(@RequestBody TimeRecordView record) {
        /*
        TODO validate time record
         */
        timeRecordDao.saveRecord(record.toTimeRecord());
    }

    /** --- Internal Methods --- */

    private ListMultimap<Integer, TimeRecord> getRecords(Integer[] empId, String from, String to, String[] status) {
        Set<Integer> empIds = new HashSet<>(Arrays.asList(empId));
        LocalDate toDate = to != null ? parseISODate(to, "to") : LocalDate.now();
        LocalDate fromDate = parseISODate(from, "from");
        Range<LocalDate> dateRange = getClosedRange(fromDate, toDate, "from", "to");
        Set<TimeRecordStatus> statuses;
        if (status != null && status.length > 0) {
            statuses = Arrays.asList(status).stream()
                    .map(recordStatus -> getEnumParameter("status", recordStatus, TimeRecordStatus.class))
                    .collect(Collectors.toSet());
        } else {
            statuses = EnumSet.allOf(TimeRecordStatus.class);
        }

        return timeRecordDao.getRecordsDuring(empIds, dateRange, statuses);
    }

    private ViewObjectResponse<?> getRecordResponse(ListMultimap<Integer, TimeRecord> records, boolean xml) {
        return new ViewObjectResponse<>(MapView.of(
                records.asMap().values().stream()
                        .map(recordList -> ListView.of(recordList.stream()
                                .map(TimeRecordView::new)
                                .collect(toList())))
                        .collect(Collectors.toMap(
                                recordList -> xml ? (Object) ("empId-" + recordList.items.get(0).getEmployeeId())
                                                  : (Object) (recordList.items.get(0).getEmployeeId()),
                                Function.identity()))
        ));
    }

}

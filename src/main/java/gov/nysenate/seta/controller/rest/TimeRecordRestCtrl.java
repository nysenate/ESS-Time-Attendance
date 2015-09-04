package gov.nysenate.seta.controller.rest;

import com.google.common.collect.*;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.response.base.SimpleResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.view.TimeRecordView;
import gov.nysenate.seta.client.view.base.ListView;
import gov.nysenate.seta.client.view.base.MapView;
import gov.nysenate.seta.dao.attendance.TimeRecordDao;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.service.accrual.AccrualInfoService;
import gov.nysenate.seta.service.attendance.InvalidTimeRecordException;
import gov.nysenate.seta.service.attendance.TimeRecordService;
import gov.nysenate.seta.service.attendance.TimeRecordValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;

@RestController
@RequestMapping(REST_PATH + "/timerecords")
public class TimeRecordRestCtrl extends BaseRestCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordRestCtrl.class);

    @Autowired TimeRecordDao timeRecordDao;
    @Autowired TimeRecordService timeRecordService;
    @Autowired AccrualInfoService accrualInfoService;

    @Autowired TimeRecordValidationService validationService;

    /**
     * Get Time Record API
     * -------------------
     *
     * Get xml or json time records for one or more employees:
     *      (GET) /api/v1/timerecords[.json]
     *
     * Request Parameters: empId - int[] - required - Records will be retrieved for these employee ids
     *                     to - Date - default current date - Gets time records that end before or on this date
     *                     from - Date - default Jan 1 on year of 'to' Date - Gets time records that begin on or after this date
     *                     status - String[] - default all statuses - Will only get time records with one of these statuses
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/xml")
    public BaseResponse getRecordsXml(@RequestParam Integer[] empId,
                                      @RequestParam(required = false) String from,
                                      @RequestParam(required = false) String to,
                                      @RequestParam(required = false) String[] status) {
        return getRecordResponse(
                getRecords(empId, from, to, status), true);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public BaseResponse getRecordsJson(@RequestParam Integer[] empId,
                                       @RequestParam(required = false) String from,
                                       @RequestParam(required = false) String to,
                                       @RequestParam(required = false) String[] status) {
        return getRecordResponse(
                getRecords(empId, from, to, status), false);
    }

    @RequestMapping(value = "/active", method = RequestMethod.GET, produces = "application/json")
    public BaseResponse getActiveRecordsJson(@RequestParam Integer[] empId,
                                             @RequestParam(required = false) String[] status) {
        return getRecordResponse(getActiveRecords(empId, status), false);
    }

    @RequestMapping(value = "/supervisor", method = RequestMethod.GET, produces = "application/json")
    public BaseResponse getActiveSupervisorRecords(@RequestParam int supId,
                                                   @RequestParam(required = false) String from,
                                                   @RequestParam(required = false) String to,
                                                   @RequestParam(required = false) String[] status)
            throws SupervisorException {
        Range<LocalDate> dateRange = parseDateRange(from, to);
        Set<TimeRecordStatus> statuses = parseStatuses(status, TimeRecordStatus.inProgress());
        return getRecordResponse(timeRecordService.getSupervisorRecords(supId, dateRange, statuses), true);
    }

    /**
     * Get Time Record Years API
     *
     * Returns the years during which the given employee has at least one time record during.
     *
     * Request Params: empId - employeeId
     */
    @RequestMapping(value = "active_years")
    public BaseResponse getTimeRecordYears(@RequestParam Integer empId) {
        return ListViewResponse.ofIntList(timeRecordDao.getTimeRecordYears(empId, SortOrder.ASC), "years");
    }

    /**
     * Save Time Record API
     * --------------------
     *
     * Save a time record:
     *      (POST) /api/v1/timerecords
     *
     * Post Data: json TimeRecordView
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public void saveRecord(@RequestBody TimeRecordView record) {
        TimeRecord newRecord = record.toTimeRecord();
        validationService.validateTimeRecord(newRecord);
        timeRecordDao.saveRecord(newRecord);
    }

    @ExceptionHandler(InvalidTimeRecordException.class)
    public BaseResponse handleInvalidTimeRecordException(InvalidTimeRecordException ex) {
        // TODO: create response from invalid record ex
        return new SimpleResponse(false, "uh oh D:", "invalid time record");
    }

    /** --- Internal Methods --- */

    private ListMultimap<Integer, TimeRecord> getRecords(Set<Integer> empIds, Range<LocalDate> dateRange,
                                                         Set<TimeRecordStatus> statuses) {
        ListMultimap<Integer, TimeRecord> records = LinkedListMultimap.create();
        timeRecordService.getTimeRecords(empIds, dateRange, statuses, true)
                .forEach(record -> records.put(record.getEmployeeId(), record));
        return records;
    }
    private ListMultimap<Integer, TimeRecord> getRecords(Integer[] empId, String from, String to, String[] status) {
        return getRecords(new HashSet<>(Arrays.asList(empId)), parseDateRange(from, to), parseStatuses(status));
    }
    private ListMultimap<Integer, TimeRecord> getActiveRecords(Integer[] empId, String[] status) {
        RangeSet<LocalDate> activePeriods = TreeRangeSet.create();
        Set<Integer> empIds = new HashSet<>(Arrays.asList(empId));
        empIds.forEach(eId ->
            accrualInfoService.getActiveAttendancePeriods(eId, LocalDate.now(), SortOrder.ASC)
                .forEach(period -> activePeriods.add(period.getDateRange())));
        return activePeriods.isEmpty() ?
                ArrayListMultimap.create() :
                getRecords(empIds, activePeriods.span(), parseStatuses(status));
    }

    private Range<LocalDate> parseDateRange(String from, String to) {
        LocalDate toDate = to != null ? parseISODate(to, "to") : LocalDate.now();
        LocalDate fromDate = from != null ? parseISODate(from, "from") : LocalDate.of(toDate.getYear(), 1, 1);
        return getClosedRange(fromDate, toDate, "from", "to");
    }

    private Set<TimeRecordStatus> parseStatuses(String[] status, Set<TimeRecordStatus> defaultValue) {
        if (status != null && status.length > 0) {
            return Arrays.asList(status).stream()
                    .map(recordStatus -> getEnumParameter("status", recordStatus, TimeRecordStatus.class))
                    .collect(Collectors.toSet());
        }
        return defaultValue;
    }

    private Set<TimeRecordStatus> parseStatuses(String[] status) {
        return parseStatuses(status, EnumSet.allOf(TimeRecordStatus.class));
    }

    /**
     * Construct a json or xml response from a timerecord multimap.  The response consists of a map of employee ids to
     * time records
     * @param records ListMultimap<Integer, TimeRecord> records
     * @param xml boolean
     * @param supervisor boolean
     * @return ViewObjectResponse
     */
    private ViewObjectResponse<?> getRecordResponse(ListMultimap<Integer, TimeRecord> records, boolean supervisor, boolean xml) {
        return new ViewObjectResponse<>(MapView.of(
                records.keySet().stream()
                        .map(id -> new AbstractMap.SimpleEntry<>(
                                        xml ? (supervisor ? "sup" : "emp") + "Id-" + id : id,
                                        ListView.of(records.get(id).stream()
                                                .map(TimeRecordView::new)
                                                .collect(Collectors.toList()))
                                )
                        )
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }
    private ViewObjectResponse<?> getRecordResponse(ListMultimap<Integer, TimeRecord> records, boolean supervisor) {
        return getRecordResponse(records, supervisor, false);
    }
}

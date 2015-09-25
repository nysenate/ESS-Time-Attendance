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
import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordScope;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.service.accrual.AccrualInfoService;
import gov.nysenate.seta.service.attendance.InvalidTimeRecordException;
import gov.nysenate.seta.service.attendance.TimeRecordManager;
import gov.nysenate.seta.service.attendance.TimeRecordService;
import gov.nysenate.seta.service.attendance.TimeRecordValidationService;
import gov.nysenate.seta.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(REST_PATH + "/timerecords")
public class TimeRecordRestCtrl extends BaseRestCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordRestCtrl.class);

    @Autowired EmployeeInfoService employeeInfoService;
    @Autowired TimeRecordService timeRecordService;
    @Autowired AccrualInfoService accrualInfoService;
    @Autowired TimeRecordManager timeRecordManager;

    @Autowired TimeRecordValidationService validationService;

    /**
     * Get Time Record API
     * -------------------
     *
     * Get time records for one or more employees:
     * (GET) /api/v1/timerecords[.json]
     *
     * Request Parameters: empId - int[] - required - Records will be retrieved for these employee ids
     *                     to - Date - default current date - Gets time records that end before or on this date
     *                     from - Date - default Jan 1 on year of 'to' Date - Gets time records that begin on or after this date
     *                     status - String[] - default all statuses - Will only get time records with one of these statuses
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public BaseResponse getRecordsJson(@RequestParam Integer[] empId,
                                       @RequestParam(required = false) String from,
                                       @RequestParam(required = false) String to,
                                       @RequestParam(required = false) String[] status) {
        return getRecordResponse(
                getRecords(empId, from, to, status), false);
    }

    /**
     * Get Active Time Record API
     * --------------------------
     *
     * @param empId
     * @param scope
     * @return
     */
    @RequestMapping(value = "/active", method = RequestMethod.GET, produces = "application/json")
    public BaseResponse getActiveRecords(@RequestParam Integer[] empId,
                                         @RequestParam(required = false) String[] scope) {
        Set<TimeRecordScope> scopes = (scope != null)
            ? Stream.of(scope).map(TimeRecordScope::getScopeFromCode).collect(Collectors.toSet())
            : Sets.newHashSet(TimeRecordScope.EMPLOYEE, TimeRecordScope.SUPERVISOR);
        ListMultimap<Integer, TimeRecord> activeRecsPerEmp = ArrayListMultimap.create();
        Set<Integer> empIdSet = new HashSet<>(Arrays.asList(empId));
        empIdSet.forEach(eid ->
            activeRecsPerEmp.putAll(eid, timeRecordService.getActiveTimeRecords(eid).stream()
                .filter(tr -> scopes.contains(tr.getRecordStatus().getScope()))
                .collect(toList())));
        return getRecordResponse(activeRecsPerEmp, false);
    }

    /**
     * Get Active Supervisor Record
     * ----------------------------
     *
     * @param supId
     * @param from
     * @param to
     * @param status
     * @return
     * @throws SupervisorException
     */
    @RequestMapping(value = "/supervisor", method = RequestMethod.GET, produces = "application/json")
    public BaseResponse getActiveSupervisorRecords(@RequestParam int supId,
                                                   @RequestParam(required = false) String from,
                                                   @RequestParam(required = false) String to,
                                                   @RequestParam(required = false) String[] status)
            throws SupervisorException {
        Range<LocalDate> dateRange = parseDateRange(from, to);
        Set<TimeRecordStatus> statuses = parseStatuses(status, TimeRecordStatus.inProgress());
        return getRecordResponse(timeRecordService.getSupervisorRecords(supId, dateRange, statuses), false);
    }

    @RequestMapping(value = "/supervisor/count", method = RequestMethod.GET, produces = "application/json")
    public BaseResponse getActiveSupervisorRecordCount(@RequestParam int supId,
                                                       @RequestParam(required = false) String from,
                                                       @RequestParam(required = false) String to,
                                                       @RequestParam(required = false) String[] status) {
        Range<LocalDate> dateRange = parseDateRange(from, to);
        Set<TimeRecordStatus> statuses = parseStatuses(status, TimeRecordStatus.inProgress());
        return new ViewObjectResponse<>(new ViewObject() {
            public Integer getCount() throws SupervisorException {
                return timeRecordService.getSupervisorRecords(supId, dateRange, statuses).size();
            }
            @Override
            public String getViewType() {
                return "supervisor record count";
            }
        });
    }

    /**
     * Get Time Record Years API
     * -------------------------
     *
     * Returns the years during which the given employee has at least one time record during.
     *
     * Request Params: empId - employeeId
     */
    @RequestMapping(value = "activeYears")
    public BaseResponse getTimeRecordYears(@RequestParam Integer empId) {
        return ListViewResponse.ofIntList(timeRecordService.getTimeRecordYears(empId, SortOrder.ASC), "years");
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
        timeRecordService.updateExistingRecord(newRecord);
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
        timeRecordService.getTimeRecords(empIds, dateRange, statuses)
                .forEach(record -> records.put(record.getEmployeeId(), record));
        return records;
    }

    private ListMultimap<Integer, TimeRecord> getRecords(Integer[] empId, String from, String to, String[] status) {
        return getRecords(new HashSet<>(Arrays.asList(empId)), parseDateRange(from, to), parseStatuses(status));
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
     *
     * @param records ListMultimap<Integer, TimeRecord> records
     * @param xml boolean
     * @param supervisor boolean
     * @return ViewObjectResponse
     */
    private ViewObjectResponse<?> getRecordResponse(ListMultimap<Integer, TimeRecord> records, boolean supervisor, boolean xml) {
        Set<Integer> empIdSet = new HashSet<>();
        records.values().stream().forEach(tr -> {
            empIdSet.add(tr.getEmployeeId());
            empIdSet.add(tr.getSupervisorId());
        });
        Map<Integer, Employee> empMap = employeeInfoService.getEmployees(empIdSet);
        return new ViewObjectResponse<>(MapView.of(
            records.keySet().stream()
                .map(id -> new AbstractMap.SimpleEntry<>((xml) ? (supervisor ? "sup" : "emp") + "Id-" + id : id,
                                ListView.of(records.get(id).stream()
                                        .map(tr -> new TimeRecordView(tr, empMap.get(tr.getEmployeeId()), empMap.get(tr.getSupervisorId())))
                                        .collect(toList())))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }

    private ViewObjectResponse<?> getRecordResponse(ListMultimap<Integer, TimeRecord> records, boolean supervisor) {
        return getRecordResponse(records, supervisor, false);
    }
}
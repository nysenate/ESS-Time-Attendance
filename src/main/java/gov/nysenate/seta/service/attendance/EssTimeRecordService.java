package gov.nysenate.seta.service.attendance;

import com.google.common.collect.*;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.RangeUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeSupInfo;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.base.SqlDaoBackedService;
import gov.nysenate.seta.service.personnel.EmployeeInfoService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EssTimeRecordService extends SqlDaoBackedService implements TimeRecordService
{
    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordService.class);

    @Autowired public EmployeeInfoService empInfoService;
    @Autowired public EmpTransactionService transService;

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getTimeRecords(Set<Integer> empIds, Range<LocalDate> dateRange,
                                           Set<TimeRecordStatus> statuses,
                                           boolean fillMissingRecords) {
        TreeMultimap<PayPeriod, TimeRecord> records = TreeMultimap.create();
        timeRecordDao.getRecordsDuring(empIds, dateRange, EnumSet.allOf(TimeRecordStatus.class)).values().stream()
                .forEach(rec -> records.put(rec.getPayPeriod(), rec));
        if (fillMissingRecords && statuses.contains(TimeRecordStatus.NOT_SUBMITTED)) {
            empIds.forEach(empId -> fillMissingRecords(empId, records, dateRange));
        }
        addSupervisors(records.values());
        return records.values().stream()
                .filter(record -> statuses.contains(record.getRecordStatus()))
                .peek(this::initializeEntries)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public ListMultimap<Integer, TimeRecord> getSupervisorRecords(int supId, Range<LocalDate> dateRange,
                                                                  Set<TimeRecordStatus> statuses)
            throws SupervisorException {
        SupervisorEmpGroup empGroup = supervisorDao.getSupervisorEmpGroup(supId, dateRange);
        ListMultimap<Integer, TimeRecord> records = ArrayListMultimap.create();

        // Get and addUsage primary employee time records
        records.putAll(supId,
                getTimeRecordsForSupInfos(empGroup.getPrimaryEmployees().values(), dateRange, statuses));

        // Get and addUsage override employee time records
        empGroup.getOverrideSupIds().forEach(overrideSupId -> {
                    records.putAll(overrideSupId,
                            getTimeRecordsForSupInfos(empGroup.getSupOverrideEmployees(overrideSupId).values(),
                                    dateRange, statuses));
                });

        return records;
    }

    /**
     * TODO.. WIP
     */
    @Override
    public boolean saveRecord(TimeRecord record) {
        return false;
    }

    /** --- Internal Methods --- */

    /**
     * Detects pay periods that are not fully covered by time records for a single employee during a given date range
     * Creates and saves new records to fill these pay periods
     */
    private void fillMissingRecords(int empId, TreeMultimap<PayPeriod, TimeRecord> records, Range<LocalDate> dateRange) {
        // Todo: what if a split-triggering transaction is posted within a pay period that already has a record created
        //       eg. a record is initially created for 7/16 - 7/29, but a supervisor change occurs on 7/22
        RangeSet<LocalDate> activeDates = empInfoService.getEmployeeActiveDatesService(empId);
        List<PayPeriod> openPeriods =
                payPeriodDao.getOpenAttendancePayPeriods(empId, DateUtils.endOfDateRange(dateRange), SortOrder.ASC);
        TreeSet<PayPeriod> incompletePeriods = openPeriods.stream()
                // Pay period is within the requested date range
                .filter(payPeriod -> dateRange.contains(payPeriod.getStartDate()))
                // Pay period is not covered by existing records
                .filter(payPeriod -> !payPeriod.isEnclosedBy(records.get(payPeriod)))
                // Pay period intersects with employee active dates
                .filter(payPeriod -> !activeDates.subRangeSet(payPeriod.getDateRange()).isEmpty())
                .collect(Collectors.toCollection(TreeSet::new));
        if (!incompletePeriods.isEmpty()) {
            Employee employee = employeeDao.getEmployeeById(empId);
            TransactionHistory history = transService.getTransHistory(empId);
            incompletePeriods.forEach(period ->
                    records.putAll(period, createEmptyTimeRecords(employee, period, history, records.get(period))));
        }
    }

    /**
     * Creates and saves new time records as needed for a single employee over a single pay period
     */
    private Set<TimeRecord> createEmptyTimeRecords(Employee employee, PayPeriod period, TransactionHistory fullHistory,
                                                     Set<TimeRecord> existingRecords) {
        TreeMap<LocalDate, Integer> supIds = fullHistory.getEffectiveSupervisorIds(period.getDateRange());
        TreeMap<LocalDate, PayType> payTypes = fullHistory.getEffectivePayTypes(period.getDateRange());
        TimeRecord record = new TimeRecord(employee, period.getDateRange(), period,
                payTypes.firstEntry().getValue(), supIds.firstEntry().getValue());
        timeRecordDao.saveRecord(record);
        logger.info("Created new record: {}", record.getDateRange());
        return Collections.singleton(record);
    }

    /**
     * Gets time records for the employees and durations specified by the given collection of supervisor infos
     */
    private List<TimeRecord> getTimeRecordsForSupInfos(Collection<EmployeeSupInfo> supInfos,
                                                       Range<LocalDate> dateRange, Set<TimeRecordStatus> statuses) {
        // Group employee ids by date range to reduce number of queries
        SetMultimap<Range<LocalDate>, Integer> periods = HashMultimap.create();
        supInfos.forEach(supInfo ->
                periods.put(dateRange.intersection(supInfo.getEffectiveDateRange()), supInfo.getEmpId()));
        return periods.keySet().stream()
                .flatMap(period -> getTimeRecords(periods.get(period), period, statuses, true).stream())
                .collect(Collectors.toList());
    }

    /**
     * Adds more detailed supervisor information to each of the given time records
     */
    private void addSupervisors(Collection<TimeRecord> timeRecords) {
        ListMultimap<Integer, TimeRecord> supervisorMap = LinkedListMultimap.create();
        timeRecords.forEach(record -> supervisorMap.put(record.getSupervisorId(), record));
        supervisorMap.keySet().stream()
                .map(employeeDao::getEmployeeById)
                .forEach(supervisor ->
                        supervisorMap.get(supervisor.getEmployeeId())
                                .forEach(record -> record.setSupervisor(supervisor)));
    }

    /**
     * Ensures that the given time record contains entries for each day covered
     */
    private void initializeEntries(TimeRecord timeRecord) {
        RangeMap<LocalDate, PayType> payTypeMap = null;

        for (LocalDate entryDate = timeRecord.getBeginDate(); !entryDate.isAfter(timeRecord.getEndDate());
             entryDate = entryDate.plusDays(1)) {
            if (!timeRecord.containsEntry(entryDate)) {
                if (payTypeMap == null) {
                    TransactionHistory transHistory = transService.getTransHistory(timeRecord.getEmployeeId());
                    payTypeMap = RangeUtils.toRangeMap(
                            transHistory.getEffectivePayTypes(timeRecord.getDateRange()), timeRecord.getEndDate());
                }
                timeRecord.addTimeEntry(new TimeEntry(timeRecord, payTypeMap.get(entryDate), entryDate));
            }
        }
    }
}
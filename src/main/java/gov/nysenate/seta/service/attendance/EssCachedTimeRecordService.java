package gov.nysenate.seta.service.attendance;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.RangeUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.common.WorkInProgress;
import gov.nysenate.seta.model.attendance.*;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeSupInfo;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.accrual.AccrualInfoService;
import gov.nysenate.seta.service.base.SqlDaoBackedService;
import gov.nysenate.seta.service.cache.EhCacheManageService;
import gov.nysenate.seta.service.personnel.EmployeeInfoService;
import gov.nysenate.seta.service.personnel.SupervisorInfoService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.seta.model.attendance.TimeRecordStatus.*;
import static java.util.stream.Collectors.toList;

@Service
@WorkInProgress(author = "Ash", since = "2015/09/11", desc = "Reworking methods in the class, adding caching")
public class EssCachedTimeRecordService extends SqlDaoBackedService implements TimeRecordService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedTimeRecordService.class);

    /** --- Caching / Events --- */
    @Autowired protected EventBus eventBus;
    @Autowired protected EhCacheManageService cacheManageService;
    private Cache activeRecordCache;

    /** --- Services --- */
    @Autowired protected EmployeeInfoService empInfoService;
    @Autowired protected EmpTransactionService transService;
    @Autowired protected AccrualInfoService accrualInfoService;
    @Autowired protected SupervisorInfoService supervisorInfoService;

    @PostConstruct
    public void init() {
        this.eventBus.register(this);
        this.activeRecordCache = this.cacheManageService.registerEternalCache("activeRecords");
    }

    /** Helper class to store a collection of time records in a cache. */
    protected static class TimeRecordCacheCollection
    {
        private int empId;
        private Map<BigInteger, TimeRecord> cachedTimeRecords = new LinkedHashMap<>();

        public TimeRecordCacheCollection(int empId, Collection<TimeRecord> cachedTimeRecords) {
            this.empId = empId;
            cachedTimeRecords.stream().forEach(tr -> this.cachedTimeRecords.put(tr.getTimeRecordId(), tr));
        }

        public int getEmpId() {
            return empId;
        }

        public List<TimeRecord> getTimeRecords() {
            return this.cachedTimeRecords.values().stream().collect(toList());
        }

        public void update(TimeRecord record) {
            if (record.getTimeRecordId() == null)
                throw new IllegalArgumentException("Time record must have a valid id prior to caching.");
            cachedTimeRecords.put(record.getTimeRecordId(), record);
        }

        public void remove(BigInteger timeRecId) {
            cachedTimeRecords.remove(timeRecId);
        }
    }

    /** --- TimeRecordService Implementation --- */

    /** {@inheritDoc} */
    @Override
    public List<Integer> getTimeRecordYears(Integer empId, SortOrder yearOrder) {
        return timeRecordDao.getTimeRecordYears(empId, yearOrder);
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getActiveTimeRecords(Integer empId) {
        activeRecordCache.acquireReadLockOnKey(empId);
        TimeRecordCacheCollection cachedRecs;
        Element element = activeRecordCache.get(empId);
        activeRecordCache.releaseReadLockOnKey(empId);
        if (element != null) {
            cachedRecs = (TimeRecordCacheCollection) element.getObjectValue();
        }
        else {
            List<TimeRecord> records = timeRecordDao.getRecordsDuring(empId, Range.all(), TimeRecordStatus.inProgress());
            records.forEach(this::initializeEntries);
            cachedRecs = new TimeRecordCacheCollection(empId, records);
            activeRecordCache.acquireWriteLockOnKey(empId);
            activeRecordCache.put(new Element(empId, cachedRecs));
            activeRecordCache.releaseWriteLockOnKey(empId);
        }
        return cachedRecs.getTimeRecords();
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getTimeRecords(Set<Integer> empIds, Range<LocalDate> dateRange,
                                           Set<TimeRecordStatus> statuses) {
        TreeMultimap<PayPeriod, TimeRecord> records = TreeMultimap.create();
        timeRecordDao.getRecordsDuring(empIds, dateRange, EnumSet.allOf(TimeRecordStatus.class)).values().stream()
                .forEach(rec -> records.put(rec.getPayPeriod(), rec));
//        if (fillMissingRecords && statuses.contains(TimeRecordStatus.NOT_SUBMITTED)) {
//            empIds.forEach(empId -> fillMissingRecords(empId, records, dateRange));
//        }
        return records.values().stream()
                .filter(record -> statuses.contains(record.getRecordStatus()))
                .peek(this::initializeEntries)
                .collect(toList());
    }

    @Override
    public List<TimeRecord> getTimeRecords(Set<Integer> empIds, Collection<PayPeriod> payPeriods,
                                           Set<TimeRecordStatus> statuses) {
        RangeSet<LocalDate> dateRanges = TreeRangeSet.create();
        payPeriods.forEach(period -> dateRanges.add(period.getDateRange()));
        return getTimeRecords(empIds, dateRanges.span(), statuses).stream()
                .filter(record -> dateRanges.encloses(record.getDateRange()))
                .collect(toList());
    }

    @Override
    public List<TimeRecord> getTimeRecordsWithSupervisor(Integer empId, Integer supId, Range<LocalDate> dateRange) {
        List<TimeRecord> timeRecords = getTimeRecords(Collections.singleton(empId), dateRange, TimeRecordStatus.getAll());
        return timeRecords.stream().filter(t -> t.getSupervisorId().equals(supId)).collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public ListMultimap<Integer, TimeRecord> getSupervisorRecords(int supId, Range<LocalDate> dateRange,
                                                                  Set<TimeRecordStatus> statuses)
            throws SupervisorException {

        SupervisorEmpGroup empGroup = supervisorInfoService.getSupervisorEmpGroup(supId, dateRange);
        ListMultimap<Integer, TimeRecord> records = ArrayListMultimap.create();
        empGroup.getAllEmployees().forEach(emp -> {
            records.putAll(emp.getEmpId(), getActiveTimeRecords(emp.getEmpId()).stream()
                    .filter(tr -> statuses.contains(tr.getRecordStatus()) &&
                            dateRange.contains(tr.getBeginDate()))
                    .collect(toList()));
        });
        return records;
    }

    @Override
    @Transactional(value = "remoteTxManager")
    @WorkInProgress(author = "ash", desc = "Need to test this a bit better...")
    public synchronized boolean updateExistingRecord(TimeRecord record) {
        if (record.getTimeRecordId() == null) {
            throw new TimeRecordException("Time record without a record id cannot be saved,");
        }
        boolean updated = timeRecordDao.saveRecord(record);
        if (updated) {
            int empId = record.getEmployeeId();
            activeRecordCache.acquireWriteLockOnKey(empId);
            try {
                Element elem = activeRecordCache.get(empId);
                if (elem != null && TimeRecordStatus.inProgress().contains(record.getRecordStatus())) {
                    TimeRecordCacheCollection cachedRecs = (TimeRecordCacheCollection) elem.getObjectValue();
                    cachedRecs.update(record);
                }
                else {
                    activeRecordCache.put(new Element(empId, new TimeRecordCacheCollection(empId, getActiveTimeRecords(empId))));
                }
            }
            finally {
                activeRecordCache.releaseWriteLockOnKey(empId);
            }
        }
        return updated;
    }

    @Override
    public boolean deleteRecord(BigInteger timeRecordId) {
        return timeRecordDao.deleteRecord(timeRecordId);
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
            accrualInfoService.getActiveAttendancePeriods(empId, DateUtils.endOfDateRange(dateRange), SortOrder.ASC);
        TreeSet<PayPeriod> incompletePeriods = openPeriods.stream()
                // Pay period is within the requested date range
                .filter(payPeriod -> dateRange.contains(payPeriod.getStartDate()))
                // Pay period is not covered by existing records
                .filter(payPeriod -> !payPeriod.isEnclosedBy(records.get(payPeriod)))
                // Pay period intersects with employee active dates
                .filter(payPeriod -> !activeDates.subRangeSet(payPeriod.getDateRange()).isEmpty())
                .collect(Collectors.toCollection(TreeSet::new));
        if (!incompletePeriods.isEmpty()) {
            Employee employee = empInfoService.getEmployee(empId);
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
        TimeRecord record = new TimeRecord(employee, period.getDateRange(), period
        );
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
        return periods.keySet().stream().parallel()
                .flatMap(period -> getTimeRecords(periods.get(period), period, statuses).stream())
                .collect(toList());
    }

    /**
     * Ensures that the given time record contains entries for each day covered.
     * @param timeRecord - TimeRecord
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
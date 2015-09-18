package gov.nysenate.seta.service.attendance;

import com.google.common.collect.*;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.RangeUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.common.WorkInProgress;
import gov.nysenate.seta.dao.attendance.TimeRecordDao;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.period.PayPeriodService;
import gov.nysenate.seta.service.personnel.EmployeeInfoService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@WorkInProgress(author = "Sam", since = "2015/09/15", desc = "testing time record generation methods")
@Service
public class EssTimeRecordManager implements TimeRecordManager {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordManager.class);

    @Autowired TimeRecordService timeRecordService;
    @Autowired TimeRecordDao timeRecordDao;
    @Autowired EmployeeDao employeeDao;
    @Autowired PayPeriodService payPeriodService;
    @Autowired EmpTransactionService transService;
    @Autowired EmployeeInfoService empInfoService;

    /**
     * The active period in this context is defined as the start of the current year to the current date.
     * preActivePeriod and postActivePeriod are used to extend this period to provide a buffer
     */
    private Period preActivePeriod = Period.ofMonths(1);
    private Period postActivePeriod = Period.ofMonths(1);

    /** {@inheritDoc} */
    @Override
    public int ensureRecords(int empId, Collection<PayPeriod> payPeriods) {
        List<TimeRecord> existingRecords =
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll());
        return ensureRecords(empId, payPeriods, existingRecords, true);
    }

    @Override
    public void ensureAllActiveRecords() {
        Range<LocalDate> activeDateRange = Range.closed(
                LocalDate.now().withDayOfYear(1).minus(preActivePeriod),
                LocalDate.now().plus(postActivePeriod));
        List<PayPeriod> payPeriods = payPeriodService.getPayPeriods(PayPeriodType.AF, activeDateRange, SortOrder.ASC);

        // Extend the active date range to include all dates covered by the retrieved pay periods
        RangeSet<LocalDate> periodRangeSet = TreeRangeSet.create();
        payPeriods.forEach(payPeriod -> periodRangeSet.add(payPeriod.getDateRange()));
        activeDateRange = periodRangeSet.span();

        Set<Integer> empIds = employeeDao.getActiveEmployeeIds(DateUtils.startOfDateRange(activeDateRange));
        logger.info("getting time records for {} employees over {}", empIds.size(), activeDateRange);
        ListMultimap<Integer, TimeRecord> existingRecordMap = timeRecordDao.getRecordsDuring(activeDateRange);

        int totalSaved = existingRecordMap.keySet().stream()
                .map(empId -> ensureRecords(empId, payPeriods,
                        Optional.of(existingRecordMap.get(empId)).orElse(Collections.emptyList()),
                        false))
                .reduce(0, Integer::sum);
        logger.info("saved {} records", totalSaved);
    }

    /** --- Internal Methods --- */

    /**
     * Ensure that the employee has up to date records that cover all given pay periods
     * Existing records are split/modified as needed to ensure correctness
     * If createTempRecords is false, then records will only be created for periods with annual pay work days
     */
    private int ensureRecords(int empId, Collection<PayPeriod> payPeriods, Collection<TimeRecord> existingRecords,
                              boolean createTempRecords) {
        logger.info("Generating records for {} over {} pay periods with {} existing records",
                empId, payPeriods.size(), existingRecords.size());

        // Get a set of ranges for which there should be time records
        LinkedHashSet<Range<LocalDate>> recordRanges = getRecordRanges(payPeriods, empId);
        List<TimeRecord> recordsToSave = new LinkedList<>();

        // Check that existing records correspond to the record ranges
        // Split any records that span multiple ranges
        //  also ensure that existing records and entries contain up to date information
        List<TimeRecord> patchedRecords = patchExistingRecords(empId, recordRanges, existingRecords);
        patchedRecords = patchedRecords.stream()
                .filter(record -> createTempRecords || !record.isEmpty())
                .peek(recordsToSave::add)
                .collect(Collectors.toList());

        // Create new records for all ranges not covered by existing records
        // TODO: prevent creation of new records if the employee is new/reappointed
        // todo    and doesn't have the necessary transactions yet
        recordRanges.stream()
                .filter(range -> createTempRecords || isRADuringRange(empId, range))
                .map(range -> createTimeRecord(empId, range))
                .forEach(recordsToSave::add);

        recordsToSave.forEach(record -> {
            if (record.getTimeRecordId() != null) {
                timeRecordService.updateExistingRecord(record);
            } else {
                timeRecordDao.saveRecord(record);
            }
        });
        logger.info("Saved {} records for {}:\t{} new\t{} patched/split",
                recordsToSave.size(), empId, recordRanges.size(), patchedRecords.size());
        return recordsToSave.size();
    }

    /**
     * Check existing records to make sure that records correspond with the computed record date ranges,
     *  and contain correct information
     * As existing records are checked, corresponding covered ranges are removed from recordRanges
     * Records that do not check out are modified accordingly
     * @return List<TimeRecord> - a list of existing records that were modified
     */
    private List<TimeRecord> patchExistingRecords(
            int empId, LinkedHashSet<Range<LocalDate>> recordRanges, Collection<TimeRecord> existingRecords) {
        List<TimeRecord> recordsToSave = new LinkedList<>();
        existingRecords.forEach(record -> {
            List<Range<LocalDate>> rangesUnderRecord = recordRanges.stream()
                    .filter(range -> range.isConnected(record.getDateRange()) &&
                            !range.intersection(record.getDateRange()).isEmpty())
                    .collect(Collectors.toList());
            if (rangesUnderRecord.size() > 1) {
                recordsToSave.addAll(splitRecord(rangesUnderRecord, record, empId));
            } else if (patchRecord(record)) {
                recordsToSave.add(record);
            }
            recordRanges.removeAll(rangesUnderRecord);
        });
        return recordsToSave;
    }

    /**
     * Generate a new time record for the given employee id spanning the given range
     */
    private TimeRecord createTimeRecord(int empId, Range<LocalDate> dateRange) {
        return new TimeRecord(
                empInfoService.getEmployee(empId, DateUtils.startOfDateRange(dateRange)),
                dateRange,
                payPeriodService.getPayPeriod(PayPeriodType.AF, DateUtils.startOfDateRange(dateRange))
        );
    }

    /**
     * Splits an existing time record according to the given date ranges
     * @param ranges List<Range<LocalDate>> - ranges corresponding to dates for which there should be distinct time records
     * @param record TimeRecord
     * @param empId int
     * @return List<TimeRecord> - the records resulting from the split
     */
    private List<TimeRecord> splitRecord(List<Range<LocalDate>> ranges, TimeRecord record, int empId) {
        Iterator<Range<LocalDate>> rangeIterator = ranges.iterator();
        List<TimeRecord> splitResult = new LinkedList<>();

        // First ensure that the supervisor is correct at the start of the range
        // and that the existing entries have correct pay types
        patchRecord(record);

        if (rangeIterator.hasNext()) {
            // Shorten the existing time record to match the first of the ranges
            // remove all entries occurring outside the first date range
            TreeMap<LocalDate, TimeEntry> existingEntryMap = new TreeMap<>();
            LocalDate newEndDate = DateUtils.endOfDateRange(rangeIterator.next());
            for (LocalDate date = newEndDate.plusDays(1); !date.isAfter(record.getEndDate()); date = date.plusDays(1)) {
                TimeEntry entry = record.removeEntry(date);
                if (entry != null) {
                    existingEntryMap.put(entry.getDate(), entry);
                }
            }
            record.setEndDate(newEndDate);
            splitResult.add(record);

            // Generate time records for the remaining ranges, adding the existing time records as appropriate
            rangeIterator.forEachRemaining(range -> {
                TimeRecord newRecord = createTimeRecord(empId, range);
                existingEntryMap.subMap(newRecord.getBeginDate(), true, newRecord.getEndDate(), true)
                        .values().forEach(newRecord::addTimeEntry);
                splitResult.add(newRecord);
            });
        }

        return splitResult;
    }

    /**
     * Verify that the given time record contains correct data
     * If not the record will be patched
     * @return true iff the record was patched
     */
    private boolean patchRecord(TimeRecord record) {
        boolean modifiedRecord = false;
        Employee empInfo = empInfoService.getEmployee(record.getEmployeeId(), record.getBeginDate());
        if (!record.checkEmployeeInfo(empInfo)) {
            modifiedRecord = true;
            record.setEmpInfo(empInfo);
        }

        return patchEntries(record) || modifiedRecord;
    }

    /**
     * Verify the time entries of the given record, patching them if they have incorrect pay types
     * @return true if one or more entries were patched
     */
    private boolean patchEntries(TimeRecord record) {
        boolean modifiedEntries = false;
        // Get effective pay types for the record
        TransactionHistory transHistory = transService.getTransHistory(record.getEmployeeId());
        RangeMap<LocalDate, PayType> payTypes = RangeUtils.toRangeMap(
                transHistory.getEffectivePayTypes(record.getDateRange()), DateUtils.THE_FUTURE);
        // Check the pay types for each entry
        for (TimeEntry entry : record.getTimeEntries()) {
            PayType correctPayType = payTypes.get(entry.getDate());
            if (!Objects.equals(entry.getPayType(), correctPayType)) {
                entry.setPayType(correctPayType);
                modifiedEntries = true;
            }
        }
        return modifiedEntries;
    }

    /**
     * Return true iff the employee was a Regular Annual employee at any point during the given date range
     */
    private boolean isRADuringRange(int empId, Range<LocalDate> dateRange) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        return transHistory.getEffectivePayTypes(dateRange).containsValue(PayType.RA);
    }

    /**
     * Get ranges corresponding to record dates for over a range of dates
     * Determined by pay periods, supervisor changes, and active dates of service
     */
    private LinkedHashSet<Range<LocalDate>> getRecordRanges(Collection<PayPeriod> periods, int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);

        // Get dates when there was a change of supervisor
        Set<LocalDate> newSupDates = transHistory.getEffectiveSupervisorIds(DateUtils.ALL_DATES).keySet();

        // Get active dates of service
        RangeSet<LocalDate> activeDates = empInfoService.getEmployeeActiveDatesService(empId);

        return periods.stream()
                .sorted()
                .map(PayPeriod::getDateRange)
                // split any ranges that contain dates where there was a supervisor change
                .flatMap(periodRange -> RangeUtils.splitRange(periodRange, newSupDates).stream())
                // get the intersection of each range with the active dates of service
                .flatMap(range -> activeDates.subRangeSet(range).asRanges().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

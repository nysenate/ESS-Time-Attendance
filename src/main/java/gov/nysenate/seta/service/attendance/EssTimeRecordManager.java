package gov.nysenate.seta.service.attendance;

import com.google.common.collect.*;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.RangeUtils;
import gov.nysenate.common.WorkInProgress;
import gov.nysenate.seta.dao.attendance.TimeRecordDao;
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
import java.util.*;
import java.util.stream.Collectors;

@WorkInProgress(author = "Sam", since = "2015/09/15", desc = "building and testing time record generation methods")
@Service
public class EssTimeRecordManager implements TimeRecordManager {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordManager.class);

    @Autowired TimeRecordService timeRecordService;
    @Autowired TimeRecordDao timeRecordDao;
    @Autowired PayPeriodService payPeriodService;
    @Autowired EmpTransactionService transService;
    @Autowired EmployeeInfoService empInfoService;

    @Override
    public void generateRecords(int empId, Collection<PayPeriod> payPeriods) {
        List<TimeRecord> existingRecords =
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll());
        generateRecords(empId, payPeriods, existingRecords);
    }

    /** --- Internal Methods --- */

    /**
     * Ensure that the employee has up to date records that cover all given pay periods
     * Existing records are split/modified as needed to ensure correctness
     */
    private void generateRecords(int empId, Collection<PayPeriod> payPeriods, Collection<TimeRecord> existingRecords) {
        // Get a set of ranges for which there should be time records
        List<Range<LocalDate>> recordRanges = getRecordRanges(payPeriods, empId);
        List<TimeRecord> recordsToSave = new LinkedList<>();

        // Check that existing records correspond to the record ranges
        // Split any records that span multiple ranges
        //  also ensure that existing records and entries contain up to date information
        recordsToSave.addAll(patchExistingRecords(empId, recordRanges, existingRecords));

        // Create new records for all ranges not covered by existing records
        recordRanges.stream()
                .map(range -> createTimeRecord(empId, range))
                .forEach(recordsToSave::add);

        recordsToSave.forEach(record -> {
            if (record.getTimeRecordId() != null) {
                timeRecordService.updateExistingRecord(record);
            } else {
                timeRecordDao.saveRecord(record);
            }
        });
    }

    /**
     * Check existing records to make sure that records correspond with the computed record date ranges,
     *  and contain correct information
     * As existing records are checked, corresponding covered ranges are removed from recordRanges
     * Records that do not check out are modified accordingly
     * @return List<TimeRecord> - a list of existing records that were modified
     */
    private List<TimeRecord> patchExistingRecords(
            int empId, List<Range<LocalDate>> recordRanges, Collection<TimeRecord> existingRecords) {
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
            // remove all entries occurring outside the first range
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
     * Get ranges corresponding to record dates for over a range of dates
     * Determined by pay periods, supervisor changes, and active dates of service
     */
    private List<Range<LocalDate>> getRecordRanges(Collection<PayPeriod> periods, int empId) {
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
                .collect(Collectors.toList());
    }
}

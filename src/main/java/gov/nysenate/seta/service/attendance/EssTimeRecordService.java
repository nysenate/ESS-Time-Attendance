package gov.nysenate.seta.service.attendance;

import com.google.common.collect.*;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordScope;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.base.SqlDaoBackedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class EssTimeRecordService extends SqlDaoBackedService implements TimeRecordService
{
    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordService.class);

    @Override
    public Map<TimeRecordScope, TimeRecord> getTimeRecords(int empId, Range<LocalDate> dateRange,
                                                           EnumSet<TimeRecordStatus> statuses, boolean fillMissingRecords)
        throws Exception {
        return null;
    }

    /**
     * Get the open attendance pay periods which are all the pay periods that are after the last closed out
     * attendance year. From the list of those pay periods, pull in all time records during those pay period ranges.
     * Then grab only the time records that have a status code that is scoped to the employee.
     */
//    @Override
    public List<TimeRecord> getTimeRecords(int empId, Range<LocalDate> dateRange, boolean fillMissingRecords) throws Exception {
        List<TimeRecord> activeRecords = Lists.newArrayList();
        TreeSet<PayPeriod> openPayPeriods = Sets.newTreeSet(getOpenPayPeriods(empId, DateUtils.endOfDateRange(dateRange)));
        if (!openPayPeriods.isEmpty()) {
            // Get the date range that spans the open pay periods.
            Range<LocalDate> openDateRange = Range.closed(
                    openPayPeriods.first().getStartDate(), openPayPeriods.last().getEndDate()
            );
            // Fetch the time records within the open period date range.
            timeRecordDao.getRecordsDuring(empId, openDateRange).forEach(record -> {
                openPayPeriods.remove(record.getPayPeriod());
                if (record.getRecordStatus().isUnlockedForEmployee()) {
                    activeRecords.add(record);
                }
            });
            // For any pay period that did not have a matching time record, create new time record(s) for it.
            if (!openPayPeriods.isEmpty()) {
                TransactionHistory history = empTransactionDao.getTransHistory(empId, EmpTransDaoOption.DEFAULT);
                openPayPeriods.forEach(openPeriod -> activeRecords.addAll(createEmptyTimeRecords(empId, openPeriod, history)));
            }
        }
        // Sort by earliest time record first
        activeRecords.sort((o1, o2) -> o1.getBeginDate().compareTo(o2.getBeginDate()));
        return activeRecords;
    }

    private LinkedList<PayPeriod> getOpenPayPeriods(int empId, LocalDate endDate) {
        return Lists.newLinkedList(
            payPeriodDao.getOpenAttendancePayPeriods(empId, endDate, SortOrder.ASC)
        );
    }

    /**
     * TODO.. WIP
     */
    protected List<TimeRecord> createEmptyTimeRecords(int empId, PayPeriod period, TransactionHistory fullHistory) throws RuntimeException {
        TreeMap<LocalDate, Integer> supIds = fullHistory.getEffectiveSupervisorIds(period.getDateRange());
        TreeMap<LocalDate, PayType> payTypes = fullHistory.getEffectivePayTypes(period.getDateRange());
        if (supIds.size() == 1 && payTypes.size() == 1) {
            TimeRecord record = new TimeRecord();
            record.setBeginDate(period.getStartDate());
            record.setEndDate(period.getEndDate());
            record.setEmployeeId(empId);
            record.setActive(true);
            record.setRecordStatus(TimeRecordStatus.NOT_SUBMITTED);
            record.setSupervisorId(supIds.firstEntry().getValue());
            return Lists.newArrayList(record);
        }
        else {
            throw new UnsupportedOperationException("Cannot handle splits yet.");
        }
    }

    /**
     * TODO.. WIP
     */
    @Override
    public boolean saveRecord(TimeRecord record) {
        return false;
    }
}
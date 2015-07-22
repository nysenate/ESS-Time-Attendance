package gov.nysenate.seta.service.attendance;

import com.google.common.collect.*;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.service.base.SqlDaoBackedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Service
public class EssTimeRecordService extends SqlDaoBackedService implements TimeRecordService
{
    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordService.class);

    /**
     * Get the open attendance pay periods which are all the pay periods that are after the last closed out
     * attendance year. From the list of those pay periods, pull in all time records during those pay period ranges.
     * Then grab only the time records that have a status code that is scoped to the employee.
     */
    @Override
    public ListMultimap<PayPeriod, TimeRecord> getActiveRecords(int empId, LocalDate endDate) throws Exception {
        ListMultimap<PayPeriod, TimeRecord> activeRecords = ArrayListMultimap.create();
        LinkedList<PayPeriod> openPayPeriods = Lists.newLinkedList(
            payPeriodDao.getOpenAttendancePayPeriods(empId, endDate, SortOrder.ASC)
        );
        Range<LocalDate> dateRange = Range.closed(
            openPayPeriods.getFirst().getStartDate(), openPayPeriods.getLast().getEndDate()
        );
        Deque<TimeRecord> recordQueue = Queues.newArrayDeque(
            timeRecordDao.getRecordsDuring(empId, dateRange, TimeRecordStatus.unlockedForEmployee())
        );
        for (PayPeriod period : openPayPeriods) {
            if (recordQueue.isEmpty()) {
                activeRecords.putAll(period, createEmptyTimeRecords(empId, period));
            }
            else if (period.getDateRange().contains(recordQueue.peekFirst().getBeginDate())) {
                activeRecords.put(period, recordQueue.pollFirst());
            }
        }
        return activeRecords;
    }

    /**
     * TODO..
     */
    protected List<TimeRecord> createEmptyTimeRecords(int empId, PayPeriod period) throws Exception {
        TimeRecord record = new TimeRecord();
        record.setBeginDate(period.getStartDate());
        record.setEndDate(period.getEndDate());
        record.setEmployeeId(empId);
        record.setActive(true);
        record.setRecordStatus(TimeRecordStatus.NOT_SUBMITTED);
        record.setSupervisorId(supervisorDao.getSupervisorIdForEmp(empId, period.getStartDate()));
        return Lists.newArrayList(record);
    }


    /**
     * TODO..
     */
    @Override
    public boolean saveRecord(TimeRecord record) {
        return false;
    }
}
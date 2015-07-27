package gov.nysenate.seta.service.attendance;

import com.google.common.collect.*;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.base.SqlDaoBackedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EssTimeRecordService extends SqlDaoBackedService implements TimeRecordService
{
    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordService.class);

    @Override
    public List<TimeRecord> getTimeRecords(Set<Integer> empIds, Range<LocalDate> dateRange,
                                           Set<TimeRecordStatus> statuses,
                                           boolean fillMissingRecords) {
        TreeMultimap<PayPeriod, TimeRecord> records = TreeMultimap.create();
        timeRecordDao.getRecordsDuring(empIds, dateRange, statuses).values()
                .forEach(rec -> records.put(rec.getPayPeriod(), rec));
        if (fillMissingRecords && statuses.contains(TimeRecordStatus.NOT_SUBMITTED)) {
            empIds.forEach(empId -> fillMissingRecords(empId, records, dateRange));
        }
        return new ArrayList<>(records.values());
    }

    private void fillMissingRecords(int empId, TreeMultimap<PayPeriod, TimeRecord> records, Range<LocalDate> dateRange) {
        TreeSet<PayPeriod> incompletePeriods =
                payPeriodDao.getOpenAttendancePayPeriods(empId, DateUtils.endOfDateRange(dateRange), SortOrder.ASC)
                        .stream()
                        .filter(payPeriod -> dateRange.contains(payPeriod.getStartDate()))
                        .filter(payPeriod -> !payPeriod.isEnclosedBy(records.get(payPeriod)))
                        .collect(Collectors.toCollection(TreeSet::new));
        if (!incompletePeriods.isEmpty()) {
            Employee employee = employeeDao.getEmployeeById(empId);
            TransactionHistory history = empTransactionDao.getTransHistory(empId, EmpTransDaoOption.DEFAULT);
//            incompletePeriods.forEach(period ->
//                    records.putAll(period, createEmptyTimeRecords(empId, period, history, records.get(period))));
        }
    }

    /**
     * TODO.. WIP
     */
    protected Set<TimeRecord> createEmptyTimeRecords(Employee employee, PayPeriod period, TransactionHistory fullHistory,
                                                     Set<TimeRecord> existingRecords) {
        TreeMap<LocalDate, Integer> supIds = fullHistory.getEffectiveSupervisorIds(period.getDateRange());
        TreeMap<LocalDate, PayType> payTypes = fullHistory.getEffectivePayTypes(period.getDateRange());
        if (supIds.size() == 1 && payTypes.size() == 1) {
            TimeRecord record = new TimeRecord(employee, period.getDateRange(), period, supIds.firstEntry().getValue());
            timeRecordDao.saveRecord(record);
            logger.info("Created new record: {}", record.getDateRange());
            return Collections.singleton(record);
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
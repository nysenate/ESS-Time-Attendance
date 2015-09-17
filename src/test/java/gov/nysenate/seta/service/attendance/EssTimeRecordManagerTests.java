package gov.nysenate.seta.service.attendance;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.service.period.PayPeriodService;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class EssTimeRecordManagerTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordManagerTests.class);

    @Autowired EssTimeRecordManager manager;
    @Autowired PayPeriodService periodService;
    @Autowired TimeRecordService timeRecordService;
    @Autowired EmpTransactionService transService;

    private static void printRecords(Collection<TimeRecord> records) {
        records.forEach(record -> {
            logger.info("{}", record.getDateRange());
            record.getTimeEntries().stream()
                    .filter(entry -> !entry.isEmpty())
                    .forEach(entry -> logger.info("{}: {}", entry.getDate(), entry.getDailyTotal()));
        });
    }

    @Test
    public void generateTimeRecordsTest() {
        int empId = 9052;
        int year = 2015;
        LocalDate startDate = LocalDate.of(year, 1, 1);
        List<PayPeriod> payPeriods =
                periodService.getPayPeriods(PayPeriodType.AF, Range.closedOpen(startDate, startDate.plusYears(1)), SortOrder.ASC);
        // Print existing records
        Set<TimeRecord> existingRecords =
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll())
                        .stream().map(TimeRecord::new).collect(Collectors.toSet());
        logger.info("\n-------- EXISTING RECORDS --------");
        printRecords(existingRecords);

        // Generate records
        manager.generateRecords(empId, periodService.getPayPeriods(PayPeriodType.AF,
                Range.closedOpen(LocalDate.ofYearDay(year, 1), LocalDate.ofYearDay(year + 1, 1)), SortOrder.ASC));

        // Print difference
        Set<TimeRecord> newRecords = new TreeSet<>(
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll()));
        logger.info("\n-------- NEW RECORDS --------");
        printRecords(Sets.difference(newRecords, existingRecords));
    }

}

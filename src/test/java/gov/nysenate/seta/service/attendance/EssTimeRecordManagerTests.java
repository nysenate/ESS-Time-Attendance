package gov.nysenate.seta.service.attendance;

import com.google.common.base.Stopwatch;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class EssTimeRecordManagerTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordManagerTests.class);

    @Autowired EssTimeRecordManager manager;
    @Autowired PayPeriodService periodService;
    @Autowired TimeRecordService timeRecordService;
    @Autowired EmpTransactionService transService;

    private static void printRecords(Collection<TimeRecord> records) {
        records.stream().sorted().forEach(record -> {
            logger.info("{}", record.getDateRange());
//            record.getTimeEntries().stream()
//                    .filter(entry -> !entry.isEmpty())
//                    .forEach(entry -> logger.info("{}: {}", entry.getDate(), entry.getDailyTotal()));
        });
    }

    @Test
    public void ensureRecordsTest() {
        int empId = 1491;
        int year = 2015;
        LocalDate startDate = LocalDate.of(year, 1, 1);
        Range<LocalDate> dateRange = Range.closed(startDate.minusMonths(1), LocalDate.now().plusMonths(1));
        List<PayPeriod> payPeriods =
                periodService.getPayPeriods(PayPeriodType.AF, dateRange, SortOrder.ASC);
        // Print existing records
        Set<TimeRecord> existingRecords =
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll())
                        .stream().map(TimeRecord::new).collect(Collectors.toSet());
        logger.info("-------- EXISTING RECORDS --------");
        printRecords(existingRecords);

        Stopwatch sw = Stopwatch.createStarted();
        // Generate records
        manager.ensureRecords(empId);
        logger.info("generation took {} ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));

        // Print difference
        Set<TimeRecord> newRecords = new TreeSet<>(
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll()));
        logger.info("-------- NEW RECORDS --------");
        printRecords(Sets.difference(newRecords, existingRecords));
    }

    @Test
    public void ensureAllRecordsTest() {
        manager.ensureAllActiveRecords();
    }

}

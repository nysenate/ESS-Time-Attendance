package gov.nysenate.seta.dao.attendance;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;
import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class TimeRecordDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(TimeRecordDaoTests.class);

    @Autowired private TimeRecordDao timeRecordDao;

    private static TimeRecord testRecord;
    private static LocalDate trStartDate = LocalDate.of(1990, 8, 2);
    private static LocalDate trEndDate = LocalDate.of(1990, 8, 15);

    public static void initTestRecord() {
        testRecord = new TimeRecord();

        testRecord.setEmployeeId(11423);
        testRecord.setEmployeeName("STOUFFER");
        testRecord.setActive(true);
        testRecord.setRecordStatus(TimeRecordStatus.SUBMITTED);
        testRecord.setRemarks("Hello world");
        testRecord.setSupervisorId(9896);
        testRecord.setBeginDate(trStartDate);
        testRecord.setEndDate(trEndDate);

        testRecord.setRespHeadCode("STSBAC");
        testRecord.setPayPeriod(new PayPeriod(PayPeriodType.AF, trStartDate, trEndDate, 10, true));

        for (LocalDate date = trStartDate; !date.isAfter(trEndDate); date = date.plusDays(1)) {
            testRecord.addTimeEntry(new TimeEntry(testRecord, PayType.RA, date));
        }
    }

    @PostConstruct
    public void init() {
        initTestRecord();
    }

    @Test
    public void insertTimeRecord() {
        boolean existing = false;
        try {
            TimeRecord oldRecord = timeRecordDao.getRecordsDuring(11423, Range.closed(trStartDate, trEndDate)) .get(0);
            testRecord.setTimeRecordId(oldRecord.getTimeRecordId());
            existing = true;
        } catch (IndexOutOfBoundsException ignored) {}
        Stopwatch sw = Stopwatch.createStarted();
        timeRecordDao.saveRecord(testRecord);
        logger.info( (existing ? "update" : "insert") + " time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void removeRecordTest() {
        timeRecordDao.getRecordsDuring(11423, Range.closed(trStartDate, trEndDate)).forEach(record -> {
            Stopwatch sw = Stopwatch.createStarted();
            timeRecordDao.deleteRecord(record.getTimeRecordId());
            logger.info("record removal time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        });
    }

    @Test
    public void updateTimeRecordTest() {
        removeRecordTest();
        insertTimeRecord();
        testRecord.getTimeEntries().get(0).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(1).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(2).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(3).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(4).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(5).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(6).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(7).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(8).setWorkHours(BigDecimal.ONE);
        testRecord.getTimeEntries().get(9).setWorkHours(BigDecimal.ONE);
        insertTimeRecord();
        testRecord.getTimeEntries().get(0).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(1).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(2).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(3).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(4).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(5).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(6).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(7).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(8).setWorkHours(BigDecimal.TEN);
        testRecord.getTimeEntries().get(9).setWorkHours(BigDecimal.TEN);
        insertTimeRecord();
    }

    @Test
    public void getRecordByEmployeeId() throws Exception {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(1991, 1, 1);
        logger.info(OutputUtils.toJson(timeRecordDao.getRecordsDuring(11423, Range.closed(fromDate, toDate))));
    }

    @Test
    public void testGetRecord() throws Exception {
        logger.info("{}", OutputUtils.toJson(timeRecordDao.getRecordsDuring(10976, Range.atLeast(LocalDate.of(2015, 5, 1)))));
    }

    @Test
    public void getTimeRecordTest() {
        Stopwatch sw = Stopwatch.createStarted();
        timeRecordDao.getTimeRecord(new BigInteger("39555288913054012606969560863737970844"));
        logger.info("record retrieval time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }
}

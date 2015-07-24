package gov.nysenate.seta.dao.attendance;

import com.google.common.collect.Range;
import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigInteger;
import java.time.LocalDate;

public class TimeRecordDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(TimeRecordDaoTests.class);

    @Autowired private TimeRecordDao timeRecordDao;

    public static TimeRecord testRecord;

    public static void initTestRecord() {
        testRecord = new TimeRecord();
        testRecord.setTimeRecordId(new BigInteger("11111111111111111111111111111111111111"));
        testRecord.setEmployeeId(11423);
        testRecord.setTxOriginalUserId("STOUFFER");
        testRecord.setEmployeeName("STOUFFER");
        testRecord.setTxUpdateUserId("STOUFFER");
        testRecord.setTxOriginalDate(LocalDate.of(1990, 8, 15).atStartOfDay());
        testRecord.setTxUpdateDate(LocalDate.of(1990, 8, 15).atStartOfDay());
        testRecord.setActive(true);
        testRecord.setRecordStatus(TimeRecordStatus.SUBMITTED);
        testRecord.setRemarks("Hello world");
        testRecord.setSupervisorId(9896);
        testRecord.setExceptionDetails(null);
        testRecord.setProcessedDate(LocalDate.of(1990, 8, 15));
        testRecord.setBeginDate(LocalDate.of(1990, 8, 2));
        testRecord.setEndDate(LocalDate.of(1990, 8, 15));
    }

    @PostConstruct
    public void init() {
        initTestRecord();
    }

    @Test
    public void insertTimeRecord() {
        timeRecordDao.saveRecord(testRecord);
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
}

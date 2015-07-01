package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigInteger;

public class TimeRecordDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(TimeRecordDaoTests.class);

    @Resource(name = "remoteTimeRecordDao")
    private TimeRecordDao timeRecordDao;

    public static TimeRecord testRecord;

    public static void initTestRecord() {
        testRecord = new TimeRecord();
        testRecord.setTimeRecordId(new BigInteger("11111111111111111111111111111111111111"));
        testRecord.setEmployeeId(11423);
        testRecord.setTxOriginalUserId("STOUFFER");
        testRecord.setEmployeeName("STOUFFER");
        testRecord.setTxUpdateUserId("STOUFFER");
        testRecord.setTxOriginalDate(java.time.LocalDate.of(1990, 8, 15).atStartOfDay());
        testRecord.setTxUpdateDate(java.time.LocalDate.of(1990, 8, 15).atStartOfDay());
        testRecord.setActive(true);
        testRecord.setRecordStatus(TimeRecordStatus.SUBMITTED);
        testRecord.setBeginDate(java.time.LocalDate.of(1990, 8, 14));
        testRecord.setEndDate(java.time.LocalDate.of(1990, 8, 15));
        testRecord.setRemarks("Hello world");
        testRecord.setSupervisorId(9896);
        testRecord.setExceptionDetails(null);
        testRecord.setProcessedDate(null);
    }

    @PostConstruct
    public void init() {
        initTestRecord();
    }

    @Test
    public void insertTimeRecord() {
        timeRecordDao.saveRecord(testRecord);
    }

}
package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.payroll.MiscLeaveType;
import gov.nysenate.seta.model.payroll.PayType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeEntryDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(TimeEntryDaoTests.class);

    @Resource(name = "remoteTimeEntry")
    private TimeEntryDao remoteTimeEntryDao;

    private TimeEntry testEntry;

    @PostConstruct
    private void init(){
        testEntry = new TimeEntry();
        testEntry.setEntryId(new BigInteger("11111111111111111111111111111111111111"));
        testEntry.setTimeRecordId(new BigInteger("11111111111111111111111111111111111111"));
        testEntry.setEmpId(11423);
        testEntry.setEmployeeName("STOUFFER");
        testEntry.setDate(LocalDate.of(1990, 8, 14));
        testEntry.setWorkHours(10);
        testEntry.setTravelHours(0);
        testEntry.setHolidayHours(0);
        testEntry.setVacationHours(0);
        testEntry.setPersonalHours(0);
        testEntry.setSickEmpHours(0);
        testEntry.setSickFamHours(0);
        testEntry.setMiscHours(0);
        testEntry.setMiscType(MiscLeaveType.valueOfCode(null));
        testEntry.setTxOriginalUserId("STOUFFER");
        testEntry.setTxUpdateUserId("STOUFFER");
        testEntry.setTxOriginalDate(LocalDate.of(1990, 8, 14).atStartOfDay());
        testEntry.setTxUpdateDate(LocalDate.of(1990, 8, 14).atStartOfDay());
        testEntry.setActive(true);
        testEntry.setEmpComment("was born today");
        testEntry.setPayType(PayType.RA);
    }

    @Test
    public void updateTimeEntryTest(){
        remoteTimeEntryDao.updateTimeEntry(testEntry);
    }

    @Test
    public void getTimeEntryTest(){
        updateTimeEntryTest();
        try {
            List<TimeEntry> timeEntry = remoteTimeEntryDao.getTimeEntriesByRecordId(testEntry.getTimeRecordId());
            assert (timeEntry.contains(testEntry));
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
        }
    }
}

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
        testEntry.setEntryId("11111111111111111111111111111111111111");
        testEntry.setTimeRecordId("11111111111111111111111111111111111111");
        testEntry.setEmpId(11423);
        testEntry.setDate(Date.valueOf(LocalDate.of(1990, 8, 14)));
        testEntry.setWorkHours(BigDecimal.TEN);
        testEntry.setTravelHours(BigDecimal.ZERO);
        testEntry.setHolidayHours(BigDecimal.ZERO);
        testEntry.setVacationHours(BigDecimal.ZERO);
        testEntry.setPersonalHours(BigDecimal.ZERO);
        testEntry.setSickEmpHours(BigDecimal.ZERO);
        testEntry.setSickFamHours(BigDecimal.ZERO);
        testEntry.setMiscHours(BigDecimal.ZERO);
        testEntry.setMiscType(MiscLeaveType.valueOfCode(null));
        testEntry.setTxOriginalUserId("STOUFFER");
        testEntry.setTxUpdateUserId("STOUFFER");
        testEntry.setTxOriginalDate(Timestamp.valueOf(LocalDate.of(1990, 8, 14).atStartOfDay()));
        testEntry.setTxUpdateDate(Timestamp.valueOf(LocalDate.of(1990, 8, 14).atStartOfDay()));
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
        List<TimeEntry> timeEntry = new ArrayList<>();
        try {
            timeEntry = remoteTimeEntryDao.getTimeEntriesByRecordId(Integer.parseInt(testEntry.getTimeRecordId()));
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
        }
        assert (timeEntry.contains(testEntry));
    }
}

package gov.nysenate.seta.service.attendance;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;

public class EssTimeRecordServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordServiceTests.class);

    @Autowired private EssTimeRecordService timeRecordService;

    @Test
    public void testGetActiveRecords() throws Exception {
        logger.info("{}", OutputUtils.toJson(timeRecordService.getTimeRecords(Collections.singleton(11423),
                Range.closed(LocalDate.of(2015, 1, 1), LocalDate.now()), EnumSet.allOf(TimeRecordStatus.class), true)));
    }

    @Test
    public void testGetSupervisorRecordsTest() throws Exception {
        LocalDate now = LocalDate.now();
        ListMultimap<Integer, TimeRecord> supRecords =
                timeRecordService.getSupervisorRecords(7048, Range.closed(LocalDate.of(now.getYear(), 1, 1), now));
        supRecords.keySet().forEach(supId -> logger.info("supId {}: {} records", supId, supRecords.get(supId).size()));
    }

    @Test
    public void testSaveRecord() throws Exception {

    }

    @Test
    public void testGetActiveRecords1() throws Exception {

    }

    @Test
    public void testCreateEmptyTimeRecords() throws Exception {

    }

    @Test
    public void testSaveRecord1() throws Exception {

    }
}
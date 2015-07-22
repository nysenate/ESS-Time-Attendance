package gov.nysenate.seta.service.attendance;

import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.BaseTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class EssTimeRecordServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordServiceTests.class);

    @Autowired private EssTimeRecordService timeRecordService;

    @Test
    public void testGetActiveRecords() throws Exception {
        logger.info("{}", OutputUtils.toJson(timeRecordService.getActiveRecords(10976, LocalDate.now()).asMap()));
    }

    @Test
    public void testSaveRecord() throws Exception {

    }
}
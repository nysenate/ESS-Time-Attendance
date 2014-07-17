package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class SqlRemoteRecordDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteRecordDaoTests.class);

    @Resource
    private SqlRemoteTimeRecordDao sqlRemoteTimeRecordDao;

    @Test
    public void getRecordByEmployeeId() throws Exception {
        logger.info(
            OutputUtils.toJson(sqlRemoteTimeRecordDao.getRecordsDuring(10976, new LocalDate(2014, 1, 1).toDate(),
                                                                          new LocalDate(2014, 2, 1).toDate())));
    }
}

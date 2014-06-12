package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Date;

public class SqlRemoteRecordDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteRecordDaoTests.class);

    @Resource
    private SqlRemoteRecordDao sqlRemoteRecordDao;

    @Test
    public void getRecordByEmployeeId() throws Exception {
        logger.info(
            OutputUtils.toJson(sqlRemoteRecordDao.getRecordsDuring(10976, new LocalDate(2014, 5, 1).toDate(),
                                                                          new LocalDate(2014, 6, 1).toDate())));
    }
}

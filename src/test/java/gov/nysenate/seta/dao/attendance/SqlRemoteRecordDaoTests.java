package gov.nysenate.seta.dao.attendance;

import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.BaseTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.time.LocalDate;

public class SqlRemoteRecordDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteRecordDaoTests.class);

    @Resource
    private SqlTimeRecordDao sqlTimeRecordDao;

    @Test
    public void getRecordByEmployeeId() throws Exception {
        logger.info(
            OutputUtils.toJson(sqlRemoteTimeRecordDao.getRecordsDuring(10976, LocalDate.of(2014, 1, 1),
                    LocalDate.of(2014, 2, 1))));
    }
}

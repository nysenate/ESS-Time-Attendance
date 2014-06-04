package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.payroll.SqlHolidayDao;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlHolidayDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlHolidayDaoTests.class);

    @Autowired
    protected SqlHolidayDao holidayDao;

    @Test
    public void testGetHoliday() throws Exception {
        logger.info(OutputUtils.toJson(holidayDao.getHoliday(new DateTime(2014, 1, 1, 0, 0, 0).toDate())));
    }

    @Test
    public void testGetHolidays() throws Exception {
        logger.info(OutputUtils.toJson(holidayDao.getHolidays(new DateTime(2013, 1, 1, 0, 0, 0).toDate(),
                                                              new DateTime(2014, 1, 1, 0, 0, 0).toDate())));
    }
}

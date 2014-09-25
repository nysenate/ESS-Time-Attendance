package gov.nysenate.seta.dao;

import com.google.common.collect.Range;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.dao.base.SortOrder;
import gov.nysenate.seta.dao.personnel.SqlHolidayDao;
import gov.nysenate.seta.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class SqlHolidayDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlHolidayDaoTests.class);

    @Autowired
    protected SqlHolidayDao holidayDao;

    @Test
    public void testGetHoliday() throws Exception {
        logger.info(OutputUtils.toJson(holidayDao.getHoliday(LocalDate.of(2014, 1, 1))));
    }

    @Test
    public void testGetHolidays() throws Exception {
        logger.info(OutputUtils.toJson(holidayDao.getHolidays(
                Range.closed(LocalDate.of(2013, 1, 1), LocalDate.of(2014, 1, 1)), true, SortOrder.DESC)));
    }
}
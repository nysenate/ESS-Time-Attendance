package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.util.OutputUtils;
import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class SqlPayPeriodDaoTests extends AbstractContextTests {

    private static final Logger logger = LoggerFactory.getLogger(SqlPayPeriodDaoTests.class);

    @Autowired
    private PayPeriodDao payPeriodDao;

    @Test
    public void testGetPayPeriod() throws Exception {
        logger.info(OutputUtils.toJson(payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2012, 11, 7).toDate())));
    }

    @Test
    public void testGetPayPeriods() throws Exception {
        logger.info(OutputUtils.toJson(payPeriodDao.getPayPeriods(PayPeriodType.AF,
                                      new LocalDate(2010, 9, 2).toDate(), new LocalDate(2010, 10, 13).toDate(), true)));
    }

    @Test
    public void testGetOpenPayPeriods() throws Exception {
        logger.info(OutputUtils.toJson(payPeriodDao.getOpenAttendancePayPeriods(10976, new Date(), true)));
    }

    @Test
    public void testGetPayPeriodDays() throws Exception {
        /** Regular pay period */
        PayPeriod period = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2014, 4, 23).toDate());
        Assert.assertEquals(14, period.getNumDays());

        /** Split after new year */
        period = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2014, 1, 1).toDate());
        Assert.assertEquals(1, period.getNumDays());

        /** Split before new year */
        period = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2013, 12, 31).toDate());
        Assert.assertEquals(13, period.getNumDays());
    }

    @Test
    public void testGetPayPeriodDays_checkForDaylightSavingsIssues() throws Exception {
        PayPeriod marchDSTPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2014, 3, 12).toDate());
        PayPeriod novemberDSTPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2014, 11, 5).toDate());
        Assert.assertEquals(14, marchDSTPeriod.getNumDays());
        Assert.assertEquals(14, novemberDSTPeriod.getNumDays());
    }
}

package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlAccrualDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualDaoTests.class);

    @Autowired
    SqlAccrualDao accrualDao;

    @Autowired
    PayPeriodDao payPeriodDao;

    @Test
    public void testGetAccuralSummary() throws Exception {
        PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2014,5,27).toDate());
        logger.info(OutputUtils.toJson(accrualDao.getAccuralSummary(10976, payPeriod)));
    }

    @Test
    public void testGetAccrualSummary_ReappointmentWithinLessThanAYear() throws Exception {
        PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2011,12,27).toDate());
        logger.info(OutputUtils.toJson(accrualDao.getAccuralSummary(11384, payPeriod)));
    }

    @Test
    public void testGetAccrualSummaries() throws Exception {

    }
}

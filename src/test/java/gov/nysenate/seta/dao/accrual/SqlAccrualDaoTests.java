package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.security.xsrf.XsrfValidator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SqlAccrualDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualDaoTests.class);
    protected static final Set<TransactionCode> SAL_CODES = new HashSet<>(Arrays.asList(TransactionCode.SAL, TransactionCode.RTP, TransactionCode.APP));
    @Autowired
    SqlAccrualDao accrualDao;

    @Autowired
    PayPeriodDao payPeriodDao;

    @Autowired
    XsrfValidator xsrfValidator;

    @Test
    public void testGetAccuralSummary() throws Exception {
        PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 5, 27));

                //logger.info(OutputUtils.toJson(accrualDao.getAccuralSummary(10976, payPeriod)));
    }

    @Test
    public void testGetAccrualSummary_ReappointmentWithinLessThanAYear() throws Exception {
        PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2011,12,27));
        //logger.info(OutputUtils.toJson(accrualDao.getAccuralSummary(11384, payPeriod)));
    }

    @Test
    public void testGetAccrualSummaries() throws Exception {

    }
}

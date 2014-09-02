package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import gov.nysenate.seta.model.transaction.AuditHistory;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SqlAccrualDaoTests extends AbstractContextTests {
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualDaoTests.class);
    protected static final Set<TransactionCode> SAL_CODES = new HashSet<>(Arrays.asList(TransactionCode.SAL, TransactionCode.RTP, TransactionCode.APP));

    @Autowired
    SqlAccrualDao accrualDao;

    @Autowired
    PayPeriodDao payPeriodDao;

    @Autowired
    SqlHoursDao sqlHoursDao;

    @Test
    public void testGetAccuralSummary() throws Exception {
        PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2014, 5, 27).toDate());
        //logger.info(OutputUtils.toJson(accrualDao.getAccuralSummary(10976, payPeriod)));
    }

    @Test
    public void testGetAccrualSummary_ReappointmentWithinLessThanAYear() throws Exception {
        PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, new LocalDate(2011, 12, 27).toDate());
        //logger.info(OutputUtils.toJson(accrualDao.getAccuralSummary(11384, payPeriod)));
    }

    @Test
    public void testGetAccrualSummaries() throws Exception {
        TransactionHistory transactionHistory = new TransactionHistory(45);

        transactionHistory.getTransRecords(SAL_CODES, true);
        logger.debug("TRANSACTION RECORDS:" + transactionHistory.getAllTransRecords(true));
        AuditHistory auditHistory = new AuditHistory(transactionHistory);

    }

    @Test
    public void testHoursDao() throws Exception {
        logger.debug("******************************start testHoursDao");
        BigDecimal hours = new BigDecimal("0");
        logger.debug("SA/RA HOURS:" + hours);
        hours = sqlHoursDao.getTotalHours(6221, new LocalDate(2014, 01, 01).toDate(), new LocalDate(2014, 7, 31).toDate());
        logger.debug("SA/RA HOURS(2):" + hours);
        hours = sqlHoursDao.getTotalHours(10170, new LocalDate(2014, 01, 01).toDate(), new LocalDate(2014, 7, 31).toDate());
        logger.debug("SA/RA HOURS(3):" + hours);
        hours = sqlHoursDao.getTotalHours(45, new LocalDate(2014, 01, 01).toDate(), new LocalDate(2014, 7, 31).toDate());
        logger.debug("SA/RA HOURS(4):" + hours);
    }

}

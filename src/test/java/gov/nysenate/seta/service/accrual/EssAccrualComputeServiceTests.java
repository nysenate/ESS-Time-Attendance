package gov.nysenate.seta.service.accrual;

import com.google.common.collect.Range;
import gov.nysenate.common.OutputUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.dao.period.PayPeriodDao;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class EssAccrualComputeServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualComputeServiceTests.class);

    @Autowired PayPeriodDao payPeriodDao;
    @Autowired EssAccrualComputeService accrualComputeService;

    @Test
    public void testGetAccruals() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accrualComputeService.getAccruals(10976, payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2015, 7, 29)))));
    }

    @Test
    public void testGetAccruals1() throws Exception {
        List<PayPeriod> payPeriods = payPeriodDao.getPayPeriods(
            PayPeriodType.AF, Range.closed(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 8, 1)), SortOrder.ASC);
        logger.info("{}", OutputUtils.toJson(accrualComputeService.getAccruals(10976, payPeriods)));
    }
}
package gov.nysenate.seta.service.period;

import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EssCachedPayPeriodServiceTests extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(EssCachedPayPeriodServiceTests.class);

    @Autowired private EssCachedPayPeriodService periodService;

    @Test
    public void getOpenPeriodsTest() {
        List<PayPeriod> openPayPeriods = periodService.getOpenPayPeriods(PayPeriodType.AF, 4856, SortOrder.ASC);
        logger.info("{}", openPayPeriods);
    }
}

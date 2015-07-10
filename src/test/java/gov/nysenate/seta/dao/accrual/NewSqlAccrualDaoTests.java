package gov.nysenate.seta.dao.accrual;

import com.google.common.collect.Range;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.common.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NewSqlAccrualDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(NewSqlAccrualDaoTests.class);

    @Autowired
    private NewSqlAccrualDao accDao;

    @Test
    public void testGetPeriodAccrualSummaries() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accDao.getPeriodAccrualSummaries(10976, 2015, LocalDate.of(2015, 12, 1))));
    }

    @Test
    public void testGetAnnualAccrualSummaries() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accDao.getAnnualAccrualSummaries(10976, 2015)
        ));
    }

    @Test
    public void testGetPeriodAccrualUsages() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accDao.getPeriodAccrualUsages(10976, Range.all())
        ));
    }
}

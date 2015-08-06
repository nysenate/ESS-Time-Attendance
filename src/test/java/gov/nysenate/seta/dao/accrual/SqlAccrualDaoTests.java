package gov.nysenate.seta.dao.accrual;

import com.google.common.collect.Range;
import gov.nysenate.common.LimitOffset;
import gov.nysenate.common.OutputUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.BaseTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SqlAccrualDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualDaoTests.class);

    @Autowired
    private SqlAccrualDao accDao;

    @Test
    public void testGetPeriodAccrualSummaries() throws Exception {
        logger.info("{}", OutputUtils.toJson(
                accDao.getPeriodAccruals(10976, LocalDate.of(2015, 12, 1), new LimitOffset(2), SortOrder.DESC)));
    }

    @Test
    public void testGetAnnualAccrualSummaries() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accDao.getAnnualAccruals(10976, 2015)
        ));
    }

    @Test
    public void testGetPeriodAccrualUsages() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accDao.getPeriodAccrualUsages(10976, Range.all())
        ));
    }
}

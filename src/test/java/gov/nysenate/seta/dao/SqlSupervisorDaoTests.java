package gov.nysenate.seta.dao;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class SqlSupervisorDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDaoTests.class);

    @Autowired
    private SupervisorDao supervisorDao;

    @Test
    public void testGetSupervisorIdForEmpWithDate_ReturnsCorrectSupervisorId() throws Exception {
        logger.info("{}", supervisorDao.getSupervisorIdForEmp(6221, LocalDate.of(2000, 9, 17)));
    }

    @Test
    public void testGetSupervisorChain_ReturnsSupervisorChain() throws Exception {
        logger.info("{}", OutputUtils.toJson(supervisorDao.getSupervisorChain(10976, LocalDate.now())));
    }

    @Test
    public void testGetSupEmpGroup_ReturnsEmpGroup() throws Exception {
        SupervisorEmpGroup group =
            supervisorDao.getSupervisorEmpGroup(9896, Range.closed(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 7, 31)));
        logger.info(OutputUtils.toJson(group));
    }

    @Test
    public void issupTest() {
        supervisorDao.isSupervisor(11423, DateUtils.ALL_DATES);
        Stopwatch stopwatch = Stopwatch.createStarted();
        logger.info("{}", supervisorDao.isSupervisor(11423, DateUtils.ALL_DATES));
        logger.info("{}", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void supOverrideTest() throws Exception {
        supervisorDao.setSupervisorOverride(11423, 7048, Range.closed(LocalDate.of(2015, 7, 16), LocalDate.of(2015, 7, 29)));
    }


}

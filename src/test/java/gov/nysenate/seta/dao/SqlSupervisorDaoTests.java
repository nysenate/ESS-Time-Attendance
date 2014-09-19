package gov.nysenate.seta.dao;

import com.google.common.collect.Range;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import gov.nysenate.seta.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

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
            supervisorDao.getSupervisorEmpGroup(9896, Range.closed(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 9, 11)));
        logger.info(OutputUtils.toJson(group));
    }


}

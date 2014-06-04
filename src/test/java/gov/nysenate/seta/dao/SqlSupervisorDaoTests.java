package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlSupervisorDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDaoTests.class);

    @Autowired
    private SupervisorDao supervisorDao;

    @Test
    public void testGetSupervisorIdForEmpWithDate_ReturnsCorrectSupervisorId() throws Exception {
        logger.info("{}", supervisorDao.getSupervisorIdForEmp(9896, new DateTime(2010, 3, 4, 0, 0, 0).toDate()));
    }

    @Test
    public void testGetSupervisorChain_ReturnsSupervisorChain() throws Exception {
        logger.info("{}", OutputUtils.toJson(supervisorDao.getSupervisorChain(10976, new DateTime().toDate())));
    }

    @Test
    public void testGetSupEmpGroup_ReturnsEmpGroup() throws Exception {
        SupervisorEmpGroup group = supervisorDao.getSupervisorEmpGroup(9896, new DateTime(2012, 2, 27, 0, 0, 0).toDate(), new DateTime(2014, 3, 12, 0, 0, 0).toDate());
        logger.info(OutputUtils.toJson(group));
    }


}

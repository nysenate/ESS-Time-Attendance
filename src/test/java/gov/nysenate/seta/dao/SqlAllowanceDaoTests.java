package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.allowances.AllowanceDao;
import gov.nysenate.seta.model.allowances.AllowanceUsage;
import gov.nysenate.seta.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

/**
 * Created by heitner on 6/27/2014.
 */
public class SqlAllowanceDaoTests  extends AbstractContextTests {

    private static final Logger logger = LoggerFactory.getLogger(SqlAllowanceDaoTests.class);

    @Autowired
    private AllowanceDao allowanceDao;

    @Test
    public void testGetAllowanceUsage() throws Exception {
        LinkedList<AllowanceUsage> allowanceUsages;
        allowanceUsages =allowanceDao.getAllowanceUsage(45, 2014);
        logger.debug("allowanceUsage Count:"+allowanceUsages.size()+": "+ OutputUtils.toJson(allowanceUsages));
    }

}

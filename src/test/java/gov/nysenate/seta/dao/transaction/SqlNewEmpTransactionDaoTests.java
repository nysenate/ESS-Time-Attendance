package gov.nysenate.seta.dao.transaction;

import com.google.common.collect.Sets;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.dao.base.SortOrder;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlNewEmpTransactionDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlNewEmpTransactionDaoTests.class);

    @Autowired
    private NewEmpTransactionDao transDao;

    @Test
    public void testSql() throws Exception {
        logger.info("{}", OutputUtils.toJson(transDao.getTransHistory(1719, Sets.newHashSet(TransactionCode.MAR),
                TransDaoOption.INITIALIZE_AS_APP).getAllTransRecords(SortOrder.ASC)));
    }
}
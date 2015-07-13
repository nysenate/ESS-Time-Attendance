package gov.nysenate.seta.dao.transaction;

import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlEmpTransactionDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmpTransactionDaoTests.class);

    @Autowired
    private SqlEmpTransactionDao empTransDao;

    @Test
    public void testGetTransHistoryBasic() throws Exception {
        TransactionHistory transHist = empTransDao.getTransHistory(10976);
        logger.info("{}", OutputUtils.toJson(transHist));
    }
}
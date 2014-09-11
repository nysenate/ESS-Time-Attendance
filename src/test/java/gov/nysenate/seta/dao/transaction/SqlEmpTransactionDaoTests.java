package gov.nysenate.seta.dao.transaction;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.accrual.SqlAccrualHelper;
import gov.nysenate.seta.model.transaction.AuditHistory;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static gov.nysenate.seta.model.transaction.TransactionCode.*;

public class SqlEmpTransactionDaoTests extends AbstractContextTests
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
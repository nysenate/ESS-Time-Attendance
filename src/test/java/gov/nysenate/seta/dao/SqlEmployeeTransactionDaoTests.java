package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.personnel.SqlEmployeeTransactionDao;
import gov.nysenate.seta.model.personnel.TransactionRecord;
import gov.nysenate.seta.model.personnel.TransactionType;
import gov.nysenate.seta.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static gov.nysenate.seta.model.personnel.TransactionType.*;
import static org.junit.Assert.assertNotNull;

public class SqlEmployeeTransactionDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeTransactionDaoTests.class);

    @Autowired
    private SqlEmployeeTransactionDao transHistDao;

    @Test
    public void testGetTransactionHistoryMapWithDate_Succeeds() throws Exception {
        Set<TransactionType> types = new LinkedHashSet<>(Arrays.asList(APP, RTP, MIN, TYP));
        LinkedList<TransactionRecord> recs = transHistDao.getTransHistory(10976, types, new Date()).getAllTransRecords(false);
        logger.debug(OutputUtils.toJson(recs));
    }
}
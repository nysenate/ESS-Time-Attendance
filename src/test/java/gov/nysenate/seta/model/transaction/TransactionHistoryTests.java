package gov.nysenate.seta.model.transaction;

import com.google.common.collect.Range;
import gov.nysenate.seta.BaseTests;
import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.dao.transaction.EmpTransactionDao;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static gov.nysenate.seta.dao.transaction.EmpTransDaoOption.DEFAULT;

public class TransactionHistoryTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryTests.class);

    @Autowired
    private EmpTransactionDao transactionDao;

    @Test
    public void testHasRecords() throws Exception {
//        logger.info("{}", OutputUtils.toJson(transactionDao.getTransHistory(10976, DEFAULT).getAllTransRecords(SortOrder.ASC)));
        logger.info("{}", OutputUtils.toJson(transactionDao.getTransHistory(6221, DEFAULT).getEffectiveSupervisorIds(
            Range.atMost(LocalDate.now()))));
    }

    @Test
    public void testAddTransactionRecord() throws Exception {

    }

    @Test
    public void testAddTransactionRecords() throws Exception {

    }

    @Test
    public void testGetTransRecords() throws Exception {

    }

    @Test
    public void testGetTransRecords1() throws Exception {

    }

    @Test
    public void testGetAllTransRecords() throws Exception {

    }
}

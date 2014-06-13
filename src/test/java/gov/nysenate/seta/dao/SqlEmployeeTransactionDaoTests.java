package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.transaction.SqlEmployeeTransactionDao;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

import static gov.nysenate.seta.model.transaction.TransactionCode.*;
import static org.junit.Assert.assertNotNull;

public class SqlEmployeeTransactionDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeTransactionDaoTests.class);

    @Autowired
    private SqlEmployeeTransactionDao transHistDao;

    @Test
    public void testGetTransactionHistoryMapWithDate_Succeeds() throws Exception {
        Set<TransactionCode> types = new LinkedHashSet<>(Arrays.asList(APP, RTP, MIN, TYP));
        LinkedList<TransactionRecord> recs = transHistDao.getTransHistory(10976, types, new Date()).getAllTransRecords(false);
        //logger.debug(OutputUtils.toJson(recs));
        //types = new LinkedHashSet<>(Arrays.asList(APP, RTP, SUP, ADT, SAL,  MIN, TYP));
        types = null;
        recs = transHistDao.getTransHistory(6221, types, new LocalDate(2014, 5, 1).toDate()).getAllTransRecords(true);
        Map<String, String> holdValues = null;
        for (TransactionRecord curRec : recs) {
            if (holdValues==null) {
                holdValues = curRec.getValueMap();
                logger.debug("Initial setup:"+OutputUtils.toJson(holdValues));
            }
            else {
                Map<String, String> currentValues = curRec.getValueMap();

                for (String curKey : currentValues.keySet()) {
                    holdValues.put(curKey, currentValues.get(curKey));
                }
                logger.debug("POINT IN TIME "+new SimpleDateFormat("MM/dd/yyyy").format(curRec.getEffectDate())+":"+OutputUtils.toJson(holdValues));
            }

        }
        logger.debug(OutputUtils.toJson(recs));

    }
}
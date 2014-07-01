package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.accrual.SqlAccrualHelper;
import gov.nysenate.seta.dao.transaction.SqlEmployeeTransactionDao;
import gov.nysenate.seta.model.accrual.PeriodAccrualSummary;
import gov.nysenate.seta.model.transaction.AuditHistory;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static gov.nysenate.seta.model.transaction.TransactionCode.*;

public class SqlEmployeeTransactionDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeTransactionDaoTests.class);

    @Autowired
    private SqlEmployeeTransactionDao transHistDao;

    @Test
    public void testGetTransactionHistoryMapWithDate_Succeeds() throws Exception {
        Set<TransactionCode> transactionCodes = new LinkedHashSet<>(Arrays.asList(APP, RTP, MIN, TYP));
        //LinkedList<TransactionRecord> recs = transHistDao.getTransHistory(10976, transactionCodes, new Date(), true).getAllTransRecords(false);
        //logger.debug(OutputUtils.toJson(recs));
        //transactionCodes = new LinkedHashSet<>(Arrays.asList(APP, RTP, SUP, ADT, SAL,  MIN, TYP));
        transactionCodes = null;
        logger.debug("====================================================GetTransactionHistory");
        //transHistDao.getTransHistory(45, transactionCodes, new LocalDate(2014, 5, 1).toDate(), true);
        TransactionHistory transactionHistory = transHistDao.getTransHistory(10538, transactionCodes, /*new LocalDate(2014, 5, 1).toDate()*/ new Date(), true);
        logger.debug("====================================================getAllTransRecords");
        //LinkedList<TransactionRecord> recs = transactionHistory.getAllTransRecords(true);  //getAllTransRecords(true);
        logger.debug("====================================================getAllTransRecords DONE");
        //logger.debug(OutputUtils.toJson(recs));
        //recs = transHistDao.getTransHistory(45, transactionCodes, new LocalDate(2014, 5, 1).toDate(), true).getAllTransRecords(true);
        Map<String, String> holdValues = null;
        //logger.debug(OutputUtils.toJson(recs));
        AuditHistory auditHistory = new AuditHistory();
        auditHistory.setTransactionHistory(transactionHistory);
        List<Map<String, String>> records = auditHistory.getAuditRecordsBetween(new LocalDate(2014, 3, 1).toDate(), new LocalDate(2014,6,30).toDate(),true);
        logger.debug("Audit records("+records.size()+"):"+ records);

        //logger.debug(OutputUtils.toJson(auditHistory.getAuditRecords()));
        //logger.debug("Working Days until now:"+new PeriodAccrualSummary().getWorkingDaysBetweenDates(new LocalDate(2014,1,1).toDate(), new Date()));

        logger.debug("TOTAL EXPECTED HOURS:"+ SqlAccrualHelper.getExpectedHours(transactionHistory, new LocalDate(2014, 1, 1).toDate(), new Date()));
        //logger.info("01/01/14:"+OutputUtils.toJson(auditHistory.getPointInTime(new LocalDate(2014, 01, 10).toDate())));
        //logger.info("01/01/10:"+OutputUtils.toJson(auditHistory.getPointInTime(new LocalDate(2010, 01, 10).toDate())));
        //logger.info("01/01/05:"+OutputUtils.toJson(auditHistory.getPointInTime(new LocalDate(2005, 01, 10).toDate())));
        //logger.info("01/01/00:"+OutputUtils.toJson(auditHistory.getPointInTime(new LocalDate(2000, 01, 10).toDate())));
        /*logger.info("01/01/99:"+OutputUtils.toJson(auditHistory.getPointInTime(new LocalDate(1999, 01, 10).toDate())));
        logger.info("01/01/98:"+OutputUtils.toJson(auditHistory.getPointInTime(new LocalDate(1998, 01, 10).toDate())));
        logger.info("Audit Records between 1/1/05 -1/1/10:"+auditHistory.getAuditRecordsBetween(new LocalDate(2005, 01, 10).toDate(), new LocalDate(2010, 01, 10).toDate()).size());
        logger.info("Audit Records between 1/1/00 -1/1/12:"+auditHistory.getAuditRecordsBetween(new LocalDate(2000, 01, 10).toDate(), new LocalDate(2012, 01, 10).toDate()).size());
        logger.info("Audit Records between 1/1/98 -1/1/15:"+auditHistory.getAuditRecordsBetween(new LocalDate(1998, 01, 10).toDate(), new LocalDate(2015, 01, 10).toDate()).size());*/
    }
}
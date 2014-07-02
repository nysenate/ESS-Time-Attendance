package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.allowances.AllowanceDao;
import gov.nysenate.seta.dao.transaction.SqlEmployeeTransactionDao;
import gov.nysenate.seta.model.transaction.AuditHistory;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by heitner on 6/27/2014.
 */

public class SqlAllowanceDaoTests  extends AbstractContextTests {

    private static final Logger logger = LoggerFactory.getLogger(SqlAllowanceDaoTests.class);

    @Autowired
    private AllowanceDao allowanceDao;

    @Autowired
    private SqlEmployeeTransactionDao sqlEmployeeTransactionDao;

    AuditHistory auditHistory;

    @Test
    public void testGetAllowanceUsage() throws Exception {
        Set<TransactionCode> allTransCodes = new HashSet<TransactionCode>(Arrays.asList(TransactionCode.values()));
        TransactionHistory transactionHistory = sqlEmployeeTransactionDao.getTransHistory(45,  allTransCodes);
        auditHistory = new AuditHistory(transactionHistory);
        //auditHistory.setTransactionHistory(transactionHistory);
        //logger.debug("AuditRecords:"+OutputUtils.toJson(auditHistory.getAuditRecords()));
        Map<String, String> matchValues = new HashMap<String, String>();
        matchValues.put("CDPAYTYPE", "TE");

        List<Map<String, String>> matchedAuditRecords = auditHistory.getMatchedAuditRecords(matchValues, true);
        logger.debug("matchedAuditRecords:"+OutputUtils.toJson(matchedAuditRecords));
        //auditHistory.setTransactionHistory(transactionHistory);

        //AllowanceUsage allowanceUsage;
       // allowanceUsage = allowanceDao.getAllowanceUsage(45, 2014, auditHistory);
        //TransactionHistory transactionHistory = new TransactionHistory(45);
        //AuditHistory auditHistory = new AuditHistory();
        //auditHistory.setTransactionHistory(transactionHistory);

        //logger.debug("allowanceUsage: "+ OutputUtils.toJson(allowanceUsage));
    }

}

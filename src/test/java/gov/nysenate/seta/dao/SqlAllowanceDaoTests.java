package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.accrual.SqlAccrualHelper;
import gov.nysenate.seta.dao.allowances.AllowanceDao;
import gov.nysenate.seta.dao.transaction.SqlEmpTransactionDao;
import gov.nysenate.seta.model.allowances.AllowanceUsage;
import gov.nysenate.seta.model.transaction.AuditHistory;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * Created by heitner on 6/27/2014.
 */

public class SqlAllowanceDaoTests  extends AbstractContextTests {

    private static final Logger logger = LoggerFactory.getLogger(SqlAllowanceDaoTests.class);

    @Autowired
    private AllowanceDao allowanceDao;

    @Autowired
    private SqlEmpTransactionDao sqlEmployeeTransactionDao;

    AuditHistory auditHistory;

    @Test
    public void testBigDecimalAddition() {
        double d = 10.222;
        BigDecimal curVal = new BigDecimal(d, MathContext.DECIMAL64);
        BigDecimal total = new BigDecimal(0);
        total = total.add(curVal);

        d = 21.1234;
        curVal = new BigDecimal(d, MathContext.DECIMAL64);
        total = total.add(curVal);

        logger.debug("total:"+total);
    }

    @Test
    public void testGetAllowanceUsage() throws Exception {
        Set<TransactionCode> allTransCodes = new HashSet<TransactionCode>(Arrays.asList(TransactionCode.values()));
        TransactionHistory transactionHistory = sqlEmployeeTransactionDao.getTransHistory(443,  allTransCodes);
        //auditHistory = new AuditHistory();
        //auditHistory.setTransactionHistory(transactionHistory);

        Map<String, String> matchValues = new HashMap<String, String>();
        matchValues.put("CDPAYTYPE", "SA");
//        String[] columnChangeFilter =  {"MOSALBIWKLY"}; //{"DETXNNOTEPAY"}; //{"MOSALBIWKLY"};
//        logger.debug("PAYTPE=TE:"+OutputUtils.toJson(auditHistory.getMatchedAuditRecords(matchValues, true, columnChangeFilter)));

        //auditHistory.setTransactionHistory(transactionHistory);
        //logger.debug("AuditRecords:"+OutputUtils.toJson(auditHistory.getAuditRecords()));
//        Map<String, String> matchValues = new HashMap<String, String>();
//        matchValues.put("CDPAYTYPE", "TE");
//        Map<String, String> excludeValues = new HashMap<String, String>();
//       excludeValues.put("CDAGENCY", "04210");
//        List<Map<String, String>> matchedAuditRecords = auditHistory.getMatchedAuditRecords(matchValues, false);
 //       logger.info(String.valueOf(SqlAccrualHelper.saProrate(matchedAuditRecords.get(matchedAuditRecords.size() - 1))));
        logger.debug("Expected Hours:" + SqlAccrualHelper.getExpectedHours(transactionHistory, new LocalDate(2014, 1, 1).toDate(), new LocalDate(2014, 7, 15).toDate()));

        transactionHistory = sqlEmployeeTransactionDao.getTransHistory(6221,  allTransCodes);
        logger.debug("Expected Hours RA:" + SqlAccrualHelper.getExpectedHours(transactionHistory, new LocalDate(2014, 1, 1).toDate(), new LocalDate(2014, 7, 15).toDate()));

        transactionHistory = sqlEmployeeTransactionDao.getTransHistory(11442,  allTransCodes);
        logger.debug("Expected Hours TE:" + SqlAccrualHelper.getExpectedHours(transactionHistory, new LocalDate(2014, 1, 1).toDate(), new LocalDate(2014, 7, 15).toDate()));


        //logger.debug("matchedAuditRecords:"+OutputUtils.toJson(matchedAuditRecords));
        //auditHistory.setTransactionHistory(transactionHistory);

        //AllowanceUsage allowanceUsage;
        //allowanceUsage = allowanceDao.getAllowanceUsage(45, 2014, auditHistory);

//        logger.debug(OutputUtils.toJson(allowanceUsage));//*/
        //TransactionHistory transactionHistory = new TransactionHistory(45);
        //AuditHistory auditHistory = new AuditHistory();
        //auditHistory.setTransactionHistory(transactionHistory);

        //logger.debug("allowanceUsage: "+ OutputUtils.toJson(allowanceUsage));
    }
    @Test
    public void testDoesNotContain() {

        Map<String, String> currentRecord = new HashMap<String, String>();
        currentRecord.put("CDPAYTYPE", "TE");
        currentRecord.put("EMP", "TESTEMP");
        currentRecord.put("CDLOCAT", "C415");
        currentRecord.put("CDSTATUS", "A");

        Map<String, String> excludeValues = new HashMap<String, String>();
        excludeValues.put("EMP", "TESTEMP");
        excludeValues.put("CDSTATUS", "A");
        logger.debug("doesNotContainValues returned:"+doesNotContainValues(currentRecord, excludeValues, true));
    }

    @Test
    public void testMatchOnValues() {

        Map<String, String> currentRecord = new HashMap<String, String>();
        currentRecord.put("CDPAYTYPE", "TE");
        currentRecord.put("EMP", "TESTEMP");
        currentRecord.put("CDLOCAT", "C415");
        currentRecord.put("CDSTATUS", "A");

        Map<String, String> matchValues = new HashMap<String, String>();
        matchValues.put("CDPAYTYPE", "TE");
        logger.debug("matchOnValues returned:"+matchOnValues(currentRecord, matchValues, false));
    }


    protected boolean doesNotContainValues (Map<String, String> currentValues, Map<String, String> excludeValues, boolean excludeOnAll) {
        Set<String> keySet = excludeValues.keySet();
        boolean matched = excludeOnAll;

         /*
           Exclude on All means that every value must not match in order to return true
           (default to true, if at least one value matches, then return false)
           Not Excluding on All means that at least one value does not match in order to be true
           (default to false, if at least one value matches then return true)
          */

        for (String curKey : keySet) {
            try {
                if (excludeOnAll) {
                    if (currentValues.containsKey(curKey)) {
                        if (currentValues.get(curKey) == null) {
                            if (excludeValues.get(curKey) == null) {
                                return false;
                            }
                        } else if (((String) excludeValues.get(curKey)).equals(((String) currentValues.get(curKey)))) {
                            return false;
                        }
                    }
                }
                else {
                    if (currentValues.containsKey(curKey)) {
                        if (currentValues.get(curKey) == null) {
                            if (excludeValues.get(curKey) != null) {
                                logger.debug(curKey+" current value is null so returning true");
                                return true;
                            }
                        } else if (!((String) excludeValues.get(curKey)).equals(((String) currentValues.get(curKey)))) {
                            logger.debug(curKey+": "+currentValues.get(curKey)+" != "+excludeValues.get(curKey)+" so returning true");
                            return true;
                        }
                    }
                }
            }
            catch (Exception e) {

            }
        }
        return matched;
    }


    protected boolean matchOnValues (Map<String, String> currentValues, Map<String, String> matchValues, boolean matchOnAll) {
        Set<String> keySet = matchValues.keySet();
        boolean matched = matchOnAll;

        for (String curKey : keySet) {
            try {
                if (matchOnAll) {
                    if (currentValues.containsKey(curKey)) {
                        if (currentValues.get(curKey) == null) {
                            if (matchValues.get(curKey) != null) {
                                return false;
                            }
                        } else if (!((String) matchValues.get(curKey)).equals(((String) currentValues.get(curKey)))) {
                            return false;
                        }
                    }
                    else {
                        return false;
                    }

                }
                else {
                    if (currentValues.containsKey(curKey)) {
                        if (currentValues.get(curKey) == null) {
                            if (matchValues.get(curKey) == null) {
                                return true;
                            }
                        } else if (((String) matchValues.get(curKey)).equals(((String) currentValues.get(curKey)))) {
                            return true;
                        }
                    }
                }
            }
            catch (Exception e) {

            }
        }
        return matchOnAll;
    }


}

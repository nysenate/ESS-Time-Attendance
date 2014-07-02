package gov.nysenate.seta.model.transaction;

import gov.nysenate.seta.dao.transaction.SqlEmployeeTransactionDao;
import gov.nysenate.seta.model.exception.TransactionHistoryException;
import gov.nysenate.seta.model.exception.TransactionHistoryNotFoundEx;
import gov.nysenate.seta.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The AuditHistory provides an ordered collection of TransactionRecords derived by TransactionHistory. This class is intended to be
 * used in methods that need to easily obtain a view of how an employee looks at any given time. This class was created to simplify the
 * need to obtain multiple values for a given point in time.
 */

public class AuditHistory {
    protected int employeeId = -1;
    protected Map<TransactionCode, List<TransactionRecord>> recordHistory;
    protected List<Map> auditRecords;
    TransactionHistory transactionHistory;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private static final Logger logger = LoggerFactory.getLogger(AuditHistory.class);

    public AuditHistory() {
        this.recordHistory = new HashMap<>();
    }

    public AuditHistory(int employeeId) {
        this.employeeId = employeeId;
        this.recordHistory = new HashMap<>();
    }

    public AuditHistory(TransactionHistory transactionHistory)  throws TransactionHistoryNotFoundEx, TransactionHistoryException {
        this.employeeId = transactionHistory.getEmployeeId();
        this.recordHistory = new HashMap<>();
        this.setTransactionHistory(transactionHistory);
        buildAuditTrail();
    }

    /** --- Functional Getters/Setters --- */


    /**
     * Sets transaction history and derives Audit History (flattened transaction history)
     * @param transactionHistory TransactionHistory
     */

    public void setTransactionHistory(TransactionHistory transactionHistory) throws TransactionHistoryNotFoundEx, TransactionHistoryException {
        if (transactionHistory == null) {
            if (employeeId<1) {
                throw new TransactionHistoryNotFoundEx("Transaction History cannot be set to null when employee id is not set.");
            }
            else {
                Set<TransactionCode> codes = new HashSet<TransactionCode>(Arrays.asList(TransactionCode.values()));
                SqlEmployeeTransactionDao transHistDao = new SqlEmployeeTransactionDao();
                transactionHistory = transHistDao.getTransHistory(employeeId, codes, true);
            }
        }
        this.employeeId = transactionHistory.getEmployeeId();
        this.transactionHistory = transactionHistory;
        buildAuditTrail();
    }

    /**
     * Returns transaction history and used in this Audit History
     */

    public TransactionHistory getTransactionHistory() {
        return this.transactionHistory;
    }

    /** --- Local classes --- */

    /**
     * Derives Audit History (flattened transaction history) from transaction history which
     * should have been set
     */

    protected void buildAuditTrail() throws TransactionHistoryException {
        try {
            Map<String, String> holdValues = null;
            auditRecords = new ArrayList<Map>();
            Date effectDate = null;
            logger.debug("buildAuditTrail transactionHistory:"+transactionHistory.getAllTransRecords(true).size());
            for (TransactionRecord curTrans : transactionHistory.getAllTransRecords(true)) {
                if (holdValues==null) {
                    holdValues = new HashMap<String, String>();
                    holdValues.put("EffectDate",sdf.format(curTrans.getEffectDate()));
                    holdValues.putAll(curTrans.getValueMap());
                    effectDate = curTrans.getEffectDate();
                    auditRecords.add(holdValues);
                    //logger.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-INITIAL Audit Record"+OutputUtils.toJson(holdValues));
                }
                else {
                    Map<String, String>  changedValues = curTrans.getValueMap();
                    Map<String, String> newValues = new HashMap<String, String>(holdValues);
                    // copyMap(holdValues);
                    newValues.put("EffectDate",sdf.format(curTrans.getEffectDate()));
                    for (String curKey : changedValues.keySet()) {
                        newValues.put(curKey, changedValues.get(curKey));
                    }
                    if (curTrans.getEffectDate().equals(effectDate)) {
                        auditRecords.set(auditRecords.size()-1, newValues);
                        //logger.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-UPDATING Audit Record");
                    }
                    else {
                        effectDate = curTrans.getEffectDate();
                        auditRecords.add(newValues);
                        //logger.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-INSERTING Audit Record");
                    }
                    this.compareValues(holdValues, newValues);
                    holdValues = newValues;
                }

            }
        }
        catch (Exception e) {
            throw new TransactionHistoryException(e.getMessage(),e.getCause());
        }
    }

    public void compareValues (Map<String, String> oldValues, Map<String, String> newValues) {
        Set<String> keySet = newValues.keySet();
        for (String curKey : keySet) {
            try {
               if (oldValues.containsKey(curKey)) {
                   if (newValues.get(curKey) == null) {
                       if (oldValues.get(curKey) != null) {
                           //logger.debug("&&&&&&&&&&value changed(" + curKey + "):" + oldValues.get(curKey) + " -> [NULL]");
                       }
                   } else if (!((String) newValues.get(curKey)).equals(((String) oldValues.get(curKey)))) {
                       //logger.debug("&&&&&&&&&&value changed(" + curKey + "):" + oldValues.get(curKey) + " -> " + newValues.get(curKey));
                   }
               }
               else {
                   //logger.debug("&&&&&&&&&&new value(" + curKey + "):" + newValues.get(curKey));
               }
            }
            catch (Exception e) {

            }
        }
    }

    /**
     * Check to see if a specific record matches on the values passed in.
     * @param recNum int - Record# being checked for a match
     * @param matchValues Map<String, String> - Values used to match
     * @param matchOnAll boolean - If true, the record needs to match all values in matchValues,
     *                             if false, the record needs to match at least one value in matchValues
     */

    protected boolean matchOnValues (int recNum, Map<String, String> matchValues, boolean matchOnAll) {
        Map<String, String> currentValues = this.auditRecords.get(recNum);
        return matchOnValues(currentValues, matchValues, matchOnAll);
    }

    /**
     * Check to currentValues if a specific record matches on the values passed in.
     * @param currentValues Map<String, String> - Record being checked for matches
     * @param matchValues Map<String, String> - Values used to match
     * @param matchOnAll boolean - If true, the record needs to match all values in matchValues,
     *                             if false, the record needs to match at least one value in matchValues
     */

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


    /*
     * Returns how Personnel/Payroll information looks on any given date (Point in Time)
     * @param dtpot Date - Point in Time Date
     */

    public Map<String, String> getPointInTime(Date dtpot) {
        Map<String, String> holdAuditRec = null;

        for (int x = 0; x < auditRecords.size(); x++) {
            Map<String, String> latestAuditRec = auditRecords.get(x);
            try {
                Date currentEffectDate = sdf.parse(latestAuditRec.get("EffectDate"));
                if (currentEffectDate.after(dtpot)) {
                    return holdAuditRec;
                }
                holdAuditRec = latestAuditRec;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return holdAuditRec;
    }

    /*
     * Find matched records
     * @param dtpot Date - Point in Time Date
     */

    public List<Map<String, String>> getMatchedAuditRecords(Date dtstart, Date dtend, Map<String, String> matchValues, boolean matchOnAll, boolean alwaysStartDateRecord) {
        Map<String, String> holdAuditRec = null;
        List<Map<String, String>> recordHistoryReturned = new ArrayList<Map<String, String>>();
        List<Map<String, String>> auditRecords = this.getAuditRecordsBetween(dtstart, dtend, alwaysStartDateRecord);
        for (int x = 0; x < auditRecords.size(); x++) {
            Map<String, String> latestAuditRec = auditRecords.get(x);
            try {
                if (this.matchOnValues(latestAuditRec, matchValues, matchOnAll)) {
                    recordHistoryReturned.add(latestAuditRec);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return auditRecords;
    }

    /*
     * Returns how Personnel/Payroll information looks on any given date (Point in Time)
     * @param dtpot Date - Point in Time Date
     */

    public List<Map<String, String>> getMatchedAuditRecords(Map<String, String> matchValues, boolean matchOnAll) {
        Map<String, String> holdAuditRec = null;
        List<Map<String, String>> recordHistoryReturned = new ArrayList<Map<String, String>>();
        for (int x = 0; x < auditRecords.size(); x++) {
            Map<String, String> latestAuditRec = auditRecords.get(x);
            try {
                if (this.matchOnValues(latestAuditRec, matchValues, matchOnAll)) {
                    recordHistoryReturned.add(latestAuditRec);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return recordHistoryReturned;
    }

    /*
     * Returns Audit Records given a date range. Records only show given a change in at least one field v
     * value has changed
     * @param dtstart Date - Start Date
     * @param dtend Date - End Date
     * @return List<Map<String, String>>
     */

    public List<Map<String, String>> getAuditRecordsBetween(Date dtstart, Date dtend, boolean alwaysStartDateRecord) {
        List<Map<String, String>> recordHistoryReturned = new ArrayList<Map<String, String>>();

        for (int x = 0; x < auditRecords.size(); x++) {
            Map<String, String> latestAuditRec = auditRecords.get(x);
            try {
                Date currentEffectDate = sdf.parse(latestAuditRec.get("EffectDate"));

                if (!(currentEffectDate.before(dtstart)||currentEffectDate.after(dtend))) {
                    if (alwaysStartDateRecord) {
                        if (x>0 && currentEffectDate.after(dtstart) && recordHistoryReturned.size()==0) {
                            recordHistoryReturned.add(auditRecords.get(x-1));
                        }
                    }
                    recordHistoryReturned.add(latestAuditRec);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return recordHistoryReturned;
    }

    /**
     * Returns true if any records exist in the history.
     * @return boolean
     */

    public boolean hasRecords() {
        return !auditRecords.isEmpty();
    }

    /*
     * Returns Audit Records given a date range. Records only show given a change in at least one field v
     * value has changed
     * @param dtstart Date - Start Date
     * @param dtend Date - End Date
     * @return List<Map<String, String>>
     */

    public List<Map> getAuditRecords(){
        return auditRecords;
    }

}

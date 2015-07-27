package gov.nysenate.seta.model.transaction;

import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.transaction.OldSqlEmpTransactionDao;
import gov.nysenate.seta.model.exception.TransactionHistoryException;
import gov.nysenate.seta.model.exception.TransactionHistoryNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The AuditHistory provides an ordered collection of TransactionRecords derived by TransactionHistory. This class is
 * intended to be used in methods that need to easily obtain a view of how an employee looks at any given time.
 * This class was created to simplify the need to obtain multiple values for a given point in time.
 */
public class AuditHistory
{
    protected int employeeId = -1;
    protected Map<TransactionCode, List<TransactionRecord>> recordHistory;
    protected List<Map> auditRecords;
    TransactionHistory transactionHistory;
    DateTimeFormatter auditDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
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
        setTransactionHistory(transactionHistory, null);
    }

    /**
     * Sets transaction history and derives Audit History (flattened transaction history)
     * @param transactionHistory TransactionHistory
     * @param  transactionCodes  Set<TransactionCode>
     */

    public void setTransactionHistory(TransactionHistory transactionHistory, Set<TransactionCode> transactionCodes) throws TransactionHistoryNotFoundEx, TransactionHistoryException {
        if (transactionHistory == null) {
            if (employeeId<1) {
                throw new TransactionHistoryNotFoundEx("Transaction History cannot be set to null when employee id is not set.");
            }
            else {

                Set<TransactionCode> codes = null;
                if (transactionCodes==null||transactionCodes.size()==0) {
                    codes = new HashSet<TransactionCode>(Arrays.asList(TransactionCode.values()));
                }
                else {
                    codes = transactionCodes;
                }
                OldSqlEmpTransactionDao transHistDao = new OldSqlEmpTransactionDao();
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
            logger.debug("buildAuditTrail transactionHistory:"+transactionHistory.getAllTransRecords(SortOrder.ASC).size());
            for (TransactionRecord curTrans : transactionHistory.getAllTransRecords(SortOrder.ASC)) {
                if (holdValues==null) {
                    holdValues = new HashMap<String, String>();
                    holdValues.put("EffectDate", auditDateFormat.format(curTrans.getEffectDate()));
                    holdValues.putAll(curTrans.getValueMap());
// commented out by ash                   effectDate = curTrans.getEffectDate();
                    auditRecords.add(holdValues);
                    //logger.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-INITIAL Audit Record"+OutputUtils.toJson(holdValues));
                }
                else {
                    Map<String, String>  changedValues = curTrans.getValueMap();
                    Map<String, String> newValues = new HashMap<String, String>(holdValues);
                    // copyMap(holdValues);
                    newValues.put("EffectDate", auditDateFormat.format(curTrans.getEffectDate()));
                    for (String curKey : changedValues.keySet()) {
                        newValues.put(curKey, changedValues.get(curKey));
                    }
                    if (curTrans.getEffectDate().equals(effectDate)) {
                        auditRecords.set(auditRecords.size()-1, newValues);
                        //logger.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-UPDATING Audit Record");
                    }
                    else {
          //commented out by ash              effectDate = curTrans.getEffectDate();
                        auditRecords.add(newValues);
                        //logger.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-INSERTING Audit Record");
                    }
                    this.compareValues(holdValues, newValues);
                    holdValues = newValues;
                }

            }
        }
        catch (Exception e) {
            throw new TransactionHistoryException(e.getMessage(),e);
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

    /**
     * Check to see if a specific record matches on the values passed in.
     * @param recNum int - Record# being checked for a match
     * @param matchValues Map<String, String> - Values used to match
     * @param matchOnAll boolean - If true, the record needs to exclude all values in matchValues,
     *                             if false, the record needs to exclude at least one value in matchValues     */

    protected boolean doesNotContainValues (int recNum, Map<String, String> matchValues, boolean matchOnAll) {
        Map<String, String> currentValues = this.auditRecords.get(recNum);
        return doesNotContainValues(currentValues, matchValues, matchOnAll);
    }

    /**
     * Check to currentValues if a specific record does not match on the values passed in.
     * @param currentValues Map<String, String> - Record being checked for matches
     * @param excludeValues Map<String, String> - Does not contain these Values. Depending on excludeOnAll,
     *                      either one value or all values must not match
     * @param excludeOnAll boolean - If true, every value must not match in order to return true.
     *                               If false, at least one value does not match in order to be true
     */

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

    /*
     * Returns how Personnel/Payroll information looks on any given date (Point in Time)
     * @param dtpot Date - Point in Time Date
     */

    public Map<String, String> getPointInTime(Date dtpot) {
        Map<String, String> holdAuditRec = null;

        for (int x = 0; x < auditRecords.size(); x++) {
            Map<String, String> latestAuditRec = auditRecords.get(x);
            try {
                Date currentEffectDate = DateUtils.toDate(LocalDateTime.parse(latestAuditRec.get("EffectDate"), auditDateFormat));
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
        return getMatchedAuditRecords(dtstart, dtend, matchValues, matchOnAll, alwaysStartDateRecord, null, true, null);
    }

    public List<Map<String, String>> getMatchedAuditRecords(Date dtstart, Date dtend, Map<String, String> matchValues, boolean matchOnAll, boolean alwaysStartDateRecord, String[] columnChangeFilter) {
        return getMatchedAuditRecords(dtstart, dtend, matchValues, matchOnAll, alwaysStartDateRecord, null, true, columnChangeFilter);
    }

    public List<Map<String, String>> getMatchedAuditRecords(Date dtstart, Date dtend, Map<String, String> matchValues, boolean matchOnAll, boolean alwaysStartDateRecord,  Map<String, String> excludeValues) {
        return getMatchedAuditRecords(dtstart, dtend, matchValues, matchOnAll, alwaysStartDateRecord, excludeValues, true, null);
    }

    public List<Map<String, String>> getMatchedAuditRecords(Date dtstart, Date dtend, Map<String, String> matchValues, boolean matchOnAll, boolean alwaysStartDateRecord,  Map<String, String> excludeValues, String[] columnChangeFilter) {
        return getMatchedAuditRecords(dtstart, dtend, matchValues, matchOnAll, alwaysStartDateRecord, excludeValues, true, columnChangeFilter);
    }

    public List<Map<String, String>> getMatchedAuditRecords(Date dtstart, Date dtend, Map<String, String> matchValues, boolean matchOnAll, boolean alwaysStartDateRecord,  Map<String, String> excludeValues , boolean excludeOnAll, String[] columnChangeFilter) {
        logger.debug("getMatchedAuditRecords()");
        Map<String, String> holdAuditRec = null;
        String[] lastValues = null;

        if (columnChangeFilter != null) {
            lastValues = new String[columnChangeFilter.length];
        }

        boolean previousValuesSet = false;

        List<Map<String, String>> recordHistoryReturned = new ArrayList<Map<String, String>>();
        List<Map<String, String>> auditRecords = this.getAuditRecordsBetween(dtstart, dtend, alwaysStartDateRecord);
        for (int x = 0; x < auditRecords.size(); x++) {
            Map<String, String> latestAuditRec = auditRecords.get(x);
            try {
                if (this.matchOnValues(latestAuditRec, matchValues, matchOnAll)) {
                    boolean excludesValues = false;
                    boolean columnValuesChanged = false;

                    if (columnChangeFilter==null||columnChangeFilter.length==0) {
                        columnValuesChanged = true;
                    }
                    else {
                        for (int y = 0;y < columnChangeFilter.length; y++) {
                            if (latestAuditRec.containsKey(columnChangeFilter[y])) {
                                if (!previousValuesSet) {
                                    columnValuesChanged = true;
                                    logger.debug("Previous value not set so columnValuesChanged");
                                }
                                else if (previousValuesSet && latestAuditRec.get(columnChangeFilter[y])==null && lastValues[y]!=null) {
                                    columnValuesChanged = true;
                                    logger.debug("Current Value is NULL and previous value is not NULL so columnValuesChanged");
                                }
                                else if  (previousValuesSet && latestAuditRec.get(columnChangeFilter[y])!=null && lastValues[y]==null) {
                                    logger.debug("Current Value is NOT NULL and previous value is NULL so columnValuesChanged");
                                    columnValuesChanged = true;
                                }
                                else if (previousValuesSet && !latestAuditRec.get(columnChangeFilter[y]).trim().equals(lastValues[y].trim())) {
                                    logger.debug("Current Value:'"+latestAuditRec.get(columnChangeFilter[y])+"', previous value:'"+lastValues[y]+"' so  columnValuesChanged");
                                    columnValuesChanged = true;
                                }
                                else {
                                    logger.debug("***********Current Value:'"+latestAuditRec.get(columnChangeFilter[y])+"', previous value:'"+lastValues[y]+"'  NO CHANGE");
                                }
                                lastValues[y] = latestAuditRec.get(columnChangeFilter[y]);
                                previousValuesSet = true;
                            }
                            else {
                                logger.debug("*********** Could not find "+columnChangeFilter[y]+" in current audit record.");
                            }

                        }
                    }

                    logger.debug("Matched on Values");
                    if (excludeValues==null ||excludeValues.size()==0) {
                        excludesValues = true;
                        logger.debug("        Matched on Values (no excluded values)");
                    }
                    else {
                        excludesValues = this.doesNotContainValues(latestAuditRec, excludeValues, excludeOnAll);
                        logger.debug("        Matched on Values (excludes values:"+excludesValues+")");
                    }
                    if (excludesValues && columnValuesChanged) {
                        logger.debug("        Matched on Values (excludes values:"+excludesValues+") adding record");
                        recordHistoryReturned.add(latestAuditRec);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return auditRecords;
    }


    public List<Map<String, String>> getMatchedAuditRecords(Map<String, String> matchValues, boolean matchOnAll) {
        return getMatchedAuditRecords(matchValues, matchOnAll, null, true, null);
    }

    public List<Map<String, String>> getMatchedAuditRecords(Map<String, String> matchValues, boolean matchOnAll, String[] columnChangeFilter) {
        return getMatchedAuditRecords(matchValues, matchOnAll, null, true, columnChangeFilter);
    }

    public List<Map<String, String>> getMatchedAuditRecords(Map<String, String> matchValues, boolean matchOnAll, Map<String, String> excludeValues ) {
        return getMatchedAuditRecords(matchValues, matchOnAll, excludeValues, true, null);
    }

    public List<Map<String, String>> getMatchedAuditRecords(Map<String, String> matchValues, boolean matchOnAll, Map<String, String> excludeValues, String[] columnChangeFilter ) {
        return getMatchedAuditRecords(matchValues, matchOnAll, excludeValues, true, columnChangeFilter);
    }

    /*
     * Returns how Personnel/Payroll information looks on any given date (Point in Time)
     * @param dtpot Date - Point in Time Date
     */

    public List<Map<String, String>> getMatchedAuditRecords(Map<String, String> matchValues, boolean matchOnAll,  Map<String, String> excludeValues ,boolean excludeOnAll, String[] columnChangeFilter) {
        Map<String, String> holdAuditRec = null;
        String[] lastValues = null;

        if (columnChangeFilter != null) {
            lastValues = new String[columnChangeFilter.length];
        }

        boolean previousValuesSet = false;

        List<Map<String, String>> recordHistoryReturned = new ArrayList<Map<String, String>>();

        for (int x = 0; x < auditRecords.size(); x++) {
            Map<String, String> latestAuditRec = auditRecords.get(x);
            try {
                if (this.matchOnValues(latestAuditRec, matchValues, matchOnAll)) {
                    boolean excludesValues = false;
                    boolean columnValuesChanged = false;

                    if (columnChangeFilter==null||columnChangeFilter.length==0) {
                        columnValuesChanged = true;
                    }
                    else {
                         for (int y = 0;y < columnChangeFilter.length; y++) {
                             if (latestAuditRec.containsKey(columnChangeFilter[y])) {
                                    if (!previousValuesSet) {
                                        columnValuesChanged = true;
                                        logger.debug("Previous value not set so columnValuesChanged");
                                    }
                                    else if (previousValuesSet && latestAuditRec.get(columnChangeFilter[y])==null && lastValues[y]!=null) {
                                        columnValuesChanged = true;
                                        logger.debug("Current Value is NULL and previous value is not NULL so columnValuesChanged");
                                    }
                                    else if  (previousValuesSet && latestAuditRec.get(columnChangeFilter[y])!=null && lastValues[y]==null) {
                                        logger.debug("Current Value is NOT NULL and previous value is NULL so columnValuesChanged");
                                        columnValuesChanged = true;
                                    }
                                    else if (previousValuesSet && !latestAuditRec.get(columnChangeFilter[y]).equals(lastValues[y])) {
                                        logger.debug("Current Value:'"+latestAuditRec.get(columnChangeFilter[y])+"', previous value:'"+lastValues[y]+"' so  columnValuesChanged");
                                        columnValuesChanged = true;
                                    }
                                    else {
                                        logger.debug("***********Current Value:'"+latestAuditRec.get(columnChangeFilter[y])+"', previous value:'"+lastValues[y]+"' so  NO CHANGE");
                                    }
                                 lastValues[y] = latestAuditRec.get(columnChangeFilter[y]);
                                 previousValuesSet = true;
                             }
                             else {
                                 logger.debug("*********** Could not find "+columnChangeFilter[y]+" in current audit record.");
                             }
                         }
                    }

                    logger.debug("Matched on Values");
                    if (excludeValues==null ||excludeValues.size()==0) {
                        excludesValues = true;
                        logger.debug("        Matched on Values (no excluded values)");
                    }
                    else {
                        excludesValues = this.doesNotContainValues(latestAuditRec, excludeValues, excludeOnAll);
                        logger.debug("        Matched on Values (excludes values:"+excludesValues+")");
                    }
                    if (excludesValues && columnValuesChanged) {
                        logger.debug("        Matched on Values (excludes values:" + excludesValues + ") adding record");
                        recordHistoryReturned.add(latestAuditRec);
                    }
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
                Date currentEffectDate = DateUtils.toDate(LocalDateTime.parse(latestAuditRec.get("EffectDate"), auditDateFormat));

                if (!(currentEffectDate.before(dtstart) || currentEffectDate.after(dtend))) {
                    if (alwaysStartDateRecord) {
                        if (x > 0 && currentEffectDate.after(dtstart) && recordHistoryReturned.size() == 0) {
                            recordHistoryReturned.add(auditRecords.get(x - 1));
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

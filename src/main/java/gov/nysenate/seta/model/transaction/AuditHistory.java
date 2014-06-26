package gov.nysenate.seta.model.transaction;

import gov.nysenate.seta.dao.transaction.SqlEmployeeTransactionDao;
import gov.nysenate.seta.model.exception.TransactionHistoryException;
import gov.nysenate.seta.model.exception.TransactionHistoryNotFoundEx;
import gov.nysenate.seta.util.OutputUtils;
import org.joda.time.LocalDate;
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

    public TransactionHistory getTransactionHistory() {
        return this.transactionHistory;
    }

    /** --- Local classes --- */

    protected void buildAuditTrail() throws TransactionHistoryException {
        try {
            Map<String, String> holdValues = null;
            auditRecords = new ArrayList<Map>();
            Date effectDate = null;

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

    public List<Map<String, String>> getAuditRecordsBetween(Date dtstart, Date dtend) {
        List<Map<String, String>> recordHistoryReturned = new ArrayList<Map<String, String>>();

        for (int x = 0; x < auditRecords.size(); x++) {
            Map<String, String> latestAuditRec = auditRecords.get(x);
            try {
                Date currentEffectDate = sdf.parse(latestAuditRec.get("EffectDate"));
                if (!(currentEffectDate.before(dtstart)||currentEffectDate.after(dtend))) {
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

    public List<Map> getAuditRecords(){
        return auditRecords;
    }

}

package gov.nysenate.seta.dao.transaction.mapper;

import gov.nysenate.seta.dao.base.BaseHandler;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.model.transaction.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TransHistoryHandler extends BaseHandler
{
    private static final Logger logger = LoggerFactory.getLogger(TransHistoryHandler.class);

    /** Keep a reference of the emp id since it's needed to create a TransactionHistory */
    protected int empId;

    /** Prefix for the columns */
    protected String pfx = "";

    /** Prefix for the audit columns, i.e. those that will be used in the record's value map. */
    protected String auditPfx = "";

    /** The set of transaction codes to restrict the history to. */
    protected Set<TransactionCode> transCodes;

    /** Options to indicate certain processing behaviors. */
    protected EmpTransDaoOption options;

    /** Stores the valid records. */
    protected List<TransactionRecord> records = new ArrayList<>();

    /** --- Constructors --- */

    public TransHistoryHandler(int empId, String pfx, String auditPfx, Set<TransactionCode> transCodes,
                               EmpTransDaoOption options) {
        this.empId = empId;
        this.pfx = pfx;
        this.auditPfx = auditPfx;
        this.transCodes = transCodes;
        this.options = options;
    }

    /** --- Handler --- */

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        TransactionCode code = TransactionCode.valueOf(rs.getString(pfx + "CDTRANS"));

        // If this is the first record, the options may request it to be set as APP.
        if (rs.isFirst() && options.shouldSetToApp() && !code.isAppointType()) {
            logger.debug("{} transaction will appear as 'APP' based on option: {}", code, options);
            code = TransactionCode.APP;
        }

        // If initialization of earliest record was requested, the result set will not filter
        // by the code. Thus every record after the first should get filtered out here.
        // We can return null here but make sure to remove the null records afterwards
        if (!rs.isFirst() && options.shouldInitialize() && !transCodes.contains(code)) {
            return;
        }

        TransactionRecord transRec = new TransactionRecord();
        transRec.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        transRec.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        transRec.setChangeId(rs.getInt(pfx + "NUCHANGE"));
        transRec.setTransCode(code);
        transRec.setDocumentId(rs.getString(pfx + "NUDOCUMENT"));
        transRec.setOriginalDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNORIGIN"));
        transRec.setUpdateDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNUPDATE"));
        transRec.setEffectDate(getLocalDateFromRs(rs, pfx + "DTEFFECT"));
        transRec.setAuditDate(getLocalDateTimeFromRs(rs, pfx + "AUD_DTTXNORIGIN"));
        transRec.setNote((transRec.getTransCode().getType().equals(TransactionType.PER))
                ? rs.getString(pfx + "DETXNNOTE50") : rs.getString(pfx + "DETXNNOTEPAY"));

        /**
         * The value map will contain the column -> value mappings for the db columns associated with the
         * transaction code. The appointment transactions (APP/RTP) will have value maps containing every column
         * since they represent the initial snapshot of the data.
         */
        Map<String, String> valueMap = new HashMap<>();
        List<String> columns = (code.isAppointType() || (options.shouldInitialize() && rs.isFirst()))
                ? TransactionCode.getAllDbColumnsList() : code.getDbColumnList();
        for (String col : columns) {
            valueMap.put(col.trim(), rs.getString(auditPfx + col.trim()));
        }
        transRec.setValueMap(valueMap);

        /** Add the record to the collection. */
        records.add(transRec);
    }

    /** --- Functional Getters --- */

    /**
     * Construct and returns a TransactionHistory generated from the result set.
     */
    public TransactionHistory getTransactionHistory() {
        return new TransactionHistory(empId, records);
    }
}
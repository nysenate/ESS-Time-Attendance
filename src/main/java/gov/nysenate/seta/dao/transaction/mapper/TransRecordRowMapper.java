package gov.nysenate.seta.dao.transaction.mapper;

import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.model.transaction.TransactionType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nysenate.seta.dao.base.BaseMapper.getLocalDateFromRs;
import static gov.nysenate.seta.dao.base.BaseMapper.getLocalDateTimeFromRs;

public class TransRecordRowMapper implements RowMapper<TransactionRecord>
{
    /** Prefix for the columns */
    protected String pfx = "";

    /** Prefix for the audit columns, i.e. those that will be used in the record's value map. */
    protected String auditPfx = "";

    /** Options to indicate certain processing behaviors. */
    protected EmpTransDaoOption options;

    public TransRecordRowMapper(String pfx, String auditPfx, EmpTransDaoOption options) {
        this.pfx = pfx;
        this.auditPfx = auditPfx;
        this.options = options;
    }

    @Override
    public TransactionRecord mapRow(ResultSet rs, int i) throws SQLException {
        TransactionCode code = TransactionCode.valueOf(rs.getString(pfx + "CDTRANS"));
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
        return transRec;
    }
}

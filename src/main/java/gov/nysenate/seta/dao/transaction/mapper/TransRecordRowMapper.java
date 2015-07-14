package gov.nysenate.seta.dao.transaction.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.model.transaction.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Deprecated
public class TransRecordRowMapper extends BaseRowMapper<TransactionRecord>
{
    private static final Logger logger = LoggerFactory.getLogger(TransRecordRowMapper.class);

    protected String pfx = "";
    protected String auditPfx = "";
    protected Set<TransactionCode> transCodes;
    protected EmpTransDaoOption options;

    public TransRecordRowMapper(String pfx, String auditPfx, Set<TransactionCode> transCodes,
                                EmpTransDaoOption options) {
        this.pfx = pfx;
        this.auditPfx = auditPfx;
        this.transCodes = transCodes;
        this.options = options;
    }

    /**
     * This row mapper expects that the result set is ordered by earliest date first.
     */
    @Override
    public TransactionRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionCode code = TransactionCode.valueOf(rs.getString(pfx + "CDTRANS"));

        // If this is the first record, the options may request it to be set as APP.
        if (rowNum == 0 && options.shouldSetToApp() && !code.isAppointType()) {
            logger.debug("{} transaction will appear as 'APP' based on option: {}", code, options);
            code = TransactionCode.APP;
        }

        // If initialization of earliest record was requested, the result set will not filter
        // by the code. Thus every record after the first should get filtered out here.
        // We can return null here but make sure to remove the null records afterwards
        if (rowNum != 0 && options.shouldInitialize() && !transCodes.contains(code)) {
            return null;
        }

        TransactionRecord transRec = new TransactionRecord();
        transRec.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        transRec.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        transRec.setChangeId(rs.getInt(pfx + "NUCHANGE"));
        transRec.setTransCode(code);
        transRec.setOriginalDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNORIGIN"));
        transRec.setUpdateDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNUPDATE"));
        transRec.setEffectDate(getLocalDateFromRs(rs, pfx + "DTEFFECT"));
        transRec.setNote((transRec.getTransCode().getType().equals(TransactionType.PER))
                         ? rs.getString(pfx + "DETXNNOTE50") : rs.getString(pfx + "DETXNNOTEPAY"));

        /**
         * The value map will contain the column -> value mappings for the db columns associated with the
         * transaction code. The appointment transactions (APP/RTP) will have value maps containing every column
         * since they represent the initial snapshot of the data.
         */
        Map<String, String> valueMap = new HashMap<>();
        List<String> columns = (code.isAppointType() || (options.shouldInitialize() && rowNum == 0))
                                ? TransactionCode.getAllDbColumnsList() : code.getDbColumnList();
        for (String col : columns) {
            valueMap.put(col.trim(), rs.getString(auditPfx + col.trim()));
        }

        transRec.setValueMap(valueMap);
        return transRec;
    }
}
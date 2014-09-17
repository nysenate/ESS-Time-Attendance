package gov.nysenate.seta.dao.transaction.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.dao.transaction.TransDaoOption;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TransactionRecordRowMapper extends BaseRowMapper<TransactionRecord>
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionRecordRowMapper.class);

    protected String pfx = "";
    protected String auditPfx = "";
    protected Set<TransactionCode> transCodes;
    protected TransDaoOption options;

    public TransactionRecordRowMapper(String pfx, String auditPfx, Set<TransactionCode> transCodes,
                                      TransDaoOption options) {
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
        TransactionRecord transRec = new TransactionRecord();
        transRec.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        transRec.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        transRec.setChangeId(rs.getInt(pfx + "NUCHANGE"));
        transRec.setTransCode(TransactionCode.valueOf(rs.getString(pfx + "CDTRANS")));
        transRec.setOriginalDate(getLocalDateTime(rs, pfx + "DTTXNORIGIN"));
        transRec.setUpdateDate(getLocalDateTime(rs, pfx + "DTTXNUPDATE"));
        transRec.setEffectDate(getLocalDate(rs, pfx + "DTEFFECT"));

        /**
         * The value map will contain the column -> value mappings for the db columns associated with the
         * transaction code. The appointment transactions (APP/RTP) will have value maps containing every column
         * since they represent the initial snapshot of the data.
         */
        TransactionCode code = transRec.getTransCode();
        Map<String, String> valueMap = new HashMap<>();
        List<String> columns = (code.isAppointType() || (options.shouldInitialize() && rowNum == 0))
                                ? TransactionCode.getAllDbColumnsList() : code.getDbColumnList();
        for (String col : columns) {
            valueMap.put(col.trim(), rs.getString(auditPfx + col.trim()));
        }

        if (rowNum == 0 && options.shouldSetToApp() && !code.isAppointType()) {
            logger.debug("{} transaction will appear as 'APP' based on option: {}", transRec.getTransCode(), options);
            transRec.setTransCode(TransactionCode.APP);
        }

        transRec.setValueMap(valueMap);
        return transRec;
    }
}

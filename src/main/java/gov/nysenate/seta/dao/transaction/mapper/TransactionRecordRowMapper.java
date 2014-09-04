package gov.nysenate.seta.dao.transaction.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
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
    private String pfx = "";
    private String auditPfx = "";
    private Set<TransactionCode> transCodes;
    private boolean earliestRecLikeAppoint = false;
    private boolean firstRecord = true;
    private static final Logger logger = LoggerFactory.getLogger(TransactionRecordRowMapper.class);
    private int firstChangeId = -1;

    public TransactionRecordRowMapper(String pfx, String auditPfx, TransactionCode transCode) {
        this(pfx, auditPfx, new HashSet<>(Arrays.asList(transCode)));
    }

    public TransactionRecordRowMapper(String pfx, String auditPfx, TransactionCode transCode, boolean earliestRecLikeAppoint) {
        this(pfx, auditPfx, new HashSet<>(Arrays.asList(transCode)), earliestRecLikeAppoint);
    }

    public TransactionRecordRowMapper(String pfx, String auditPfx, Set<TransactionCode> transCodes) {
        this(pfx, auditPfx, transCodes, false);
    }

    public TransactionRecordRowMapper(String pfx, String auditPfx, Set<TransactionCode> transCodes, boolean earliestRecLikeAppoint) {
        this.pfx = pfx;
        this.auditPfx = auditPfx;
        this.transCodes = transCodes;
        this.earliestRecLikeAppoint = earliestRecLikeAppoint;
    }

    @Override
    public TransactionRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionRecord transRec = new TransactionRecord();
        transRec.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        transRec.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        transRec.setChangeId(rs.getInt(pfx + "NUCHANGE"));
        transRec.setTransCode(TransactionCode.valueOf(rs.getString(pfx + "CDTRANS")));
        transRec.setOriginalDate(rs.getTimestamp(pfx + "DTTXNORIGIN"));
        transRec.setUpdateDate(rs.getTimestamp(pfx + "DTTXNUPDATE"));
        transRec.setEffectDate(rs.getDate(pfx + "DTEFFECT"));
        /**
         * The value map will contain the column -> value mappings for the db columns associated with the
         * transaction code. The appointment transactions (APP/RTP) will have value maps containing every column
         * since they represent the initial snapshot of the data.
         */
        TransactionCode code = transRec.getTransCode();
        boolean isAppointment = code.equals(TransactionCode.APP) || code.equals(TransactionCode.RTP);

        Map<String, String> valueMap = new HashMap<>();
        for (String col : ((isAppointment ||(earliestRecLikeAppoint && rowNum==0)||transRec.getChangeId()==firstChangeId) ? TransactionCode.getAllDbColumnsList() : code.getDbColumnList())) {
            valueMap.put(col.trim(), rs.getString(auditPfx + col.trim()));
        }

        if (earliestRecLikeAppoint && rowNum==0)  {
            firstChangeId = transRec.getChangeId();
        }
        else if (transRec.getChangeId()==firstChangeId) {
        }
        transRec.setValueMap(valueMap);
        if (firstRecord) {
            logger.debug(firstChangeId+" TransactionRecordRowMapper FirstRecord:"+OutputUtils.toJson(valueMap));
        }
        firstRecord = false;
        return transRec;
    }
}

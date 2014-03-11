package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TransactionRecord;
import gov.nysenate.seta.model.TransactionType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TransactionRecordRowMapper implements RowMapper<TransactionRecord>
{
    private String pfx = "";
    private String auditPfx = "";
    private Set<TransactionType> transTypes;

    public TransactionRecordRowMapper(String pfx, String auditPfx, TransactionType transType) {
        this(pfx, auditPfx, new HashSet<>(Arrays.asList(transType)));
    }

    public TransactionRecordRowMapper(String pfx, String auditPfx, Set<TransactionType> transTypes) {
        this.pfx = pfx;
        this.auditPfx = auditPfx;
        this.transTypes = transTypes;
    }

    @Override
    public TransactionRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionRecord transRec = new TransactionRecord();
        transRec.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        transRec.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        transRec.setChangeId(rs.getInt(pfx + "NUCHANGE"));
        transRec.setTransType(TransactionType.valueOf(rs.getString(pfx + "CDTRANS")));
        transRec.setOriginalDate(rs.getDate(pfx + "DTTXNORIGIN"));
        transRec.setUpdateDate(rs.getDate(pfx + "DTTXNUPDATE"));
        transRec.setEffectDate(rs.getDate(pfx + "DTEFFECT"));
        TransactionType type = transRec.getTransType();
        Map<String, String> valueMap = new HashMap<>();
        for (String col : type.getDbColumnList()) {
            valueMap.put(col.trim(), rs.getString(auditPfx + col.trim()));
        }
        transRec.setValueMap(valueMap);
        return transRec;
    }
}

package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TransactionRecord;
import gov.nysenate.seta.model.TransactionType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class TransactionRecordRowMapper implements RowMapper<TransactionRecord>
{
    private String auditPfx = "";
    private Set<TransactionType> transTypes;

    public TransactionRecordRowMapper(String auditPfx, Set<TransactionType> transTypes) {
        this.auditPfx = auditPfx;
        this.transTypes = transTypes;
    }

    public TransactionRecordRowMapper(String auditPfx, TransactionType transType) {
        this.auditPfx = auditPfx;
        this.transTypes = new HashSet<>();
        this.transTypes.add(transType);
    }

    @Override
    public TransactionRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }
}

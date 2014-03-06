package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TransactionRecord;
import gov.nysenate.seta.model.TransactionType;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class SqlTransactionHistoryDao extends SqlBaseDao implements TransactionHistoryDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlTransactionHistoryDao.class);

    protected static final String GET_LAST_TRANS_REC_SQL =
        "SELECT * FROM (\n" +
        "   SELECT aud.NUXREFEM, ptx.CDTRANS, ptx.CDTRANSTYP, ptx.NUCHANGE, ptx.DTTXNORIGIN, ptx.DTTXNUPDATE,\n" +
        "          ptx.DTEFFECT, MAX(ptx.DTEFFECT) OVER (PARTITION BY ptx.CDTRANS) AS LATEST_DTEFFECT\n" +
        "          ${audColumns}\n" +
        "   FROM PM21PERAUDIT aud\n" +
        "   LEFT JOIN PD21PTXNCODE ptx ON aud.NUCHANGE  = ptx.NUCHANGE\n" +
        "   LEFT JOIN (SELECT DISTINCT CDTRANS, CDTRANSTYP FROM PL21TRANCODE) code ON ptx.CDTRANS = code.CDTRANS\n" +
        "   WHERE aud.NUXREFEM = :empId AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT <= :dateEnd\n" +
        "   AND ptx.CDTRANS IN (:transCodes)\n" +
        "   ORDER BY ptx.DTTXNORIGIN DESC) t\n" +
        "WHERE DTEFFECT = LATEST_DTEFFECT";

    @Override
    public TransactionRecord getLastTransactionRecord(int empId, TransactionType type) {
        return null;
    }

    @Override
    public TransactionRecord getLastTransactionRecord(int empId, TransactionType type, Date end) {
        Map<String, String> selectMap = new HashMap<>();
        String auditColumns = type.getDbColumns();
        selectMap.put("audColumns", (!auditColumns.isEmpty()) ? ("," + auditColumns) : "");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("dateEnd", end);
        params.addValue("transCodes", type.name());

        StrSubstitutor strSub = new StrSubstitutor(selectMap);
        String sql = strSub.replace(GET_LAST_TRANS_REC_SQL);
        remoteNamedJdbc.query(sql, params, new RowMapper<TransactionRecord>() {
            @Override
            public TransactionRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
                logger.info(rs.getString("NUXREFEM"));
                logger.info(rs.getString("CDTRANS"));
                logger.info(rs.getString("CDTRANSTYP"));
                logger.info(rs.getString("NUCHANGE"));
                logger.info(rs.getString("DTTXNORIGIN"));
                logger.info(rs.getString("DTTXNUPDATE"));
                logger.info(rs.getString("DTEFFECT"));
                logger.info(rs.getString("NUXREFSV"));

                return null;
            }
        });
        return null;
    }

    @Override
    public Map<TransactionType, TransactionRecord> getLastTransactionRecords(int empId, Set<TransactionType> types) {
        return null;
    }

    @Override
    public Map<TransactionType, TransactionRecord> getLastTransactionRecords(int empId, Set<TransactionType> types, Date end) {
        return null;
    }
}

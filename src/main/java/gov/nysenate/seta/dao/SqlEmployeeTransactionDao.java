package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SqlEmployeeTransactionDao extends SqlBaseDao implements EmployeeTransactionDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeTransactionDao.class);

    protected static final String GET_LAST_TRANS_REC_SQL =
        "SELECT * FROM (\n" +
        "   SELECT aud.NUXREFEM, ptx.CDSTATUS, ptx.CDTRANS, ptx.CDTRANSTYP, ptx.NUCHANGE, ptx.DTTXNORIGIN, ptx.DTTXNUPDATE,\n" +
        "          ptx.DTEFFECT, MAX(ptx.DTEFFECT) OVER (PARTITION BY ptx.CDTRANS) AS LATEST_DTEFFECT,\n" +
        "          MAX(ptx.DTTXNORIGIN) OVER (PARTITION BY ptx.CDTRANS) AS LATEST_DTTXNORIGIN\n" +
        "          ${audColumns}\n" +
        "   FROM PM21PERAUDIT aud\n" +
        "   JOIN PD21PTXNCODE ptx ON aud.NUCHANGE  = ptx.NUCHANGE\n" +
        "   WHERE aud.NUXREFEM = :empId AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT BETWEEN :dateStart AND :dateEnd\n" +
        "   AND ptx.CDTRANS IN (:transCodes)\n" +
        ")\n" +
        "WHERE DTEFFECT = LATEST_DTEFFECT AND DTTXNORIGIN = LATEST_DTTXNORIGIN";

    protected static final String GET_TRANS_HISTORY_SQL =
        "SELECT aud.NUXREFEM, ptx.CDSTATUS, ptx.CDTRANS, ptx.CDTRANSTYP, ptx.NUCHANGE, ptx.DTTXNORIGIN, ptx.DTTXNUPDATE,\n" +
        "       ptx.DTEFFECT, ${audColumns}\n" +
        "FROM PM21PERAUDIT aud\n" +
        "LEFT JOIN PD21PTXNCODE ptx ON aud.NUCHANGE = ptx.NUCHANGE\n" +
        "LEFT JOIN (SELECT DISTINCT CDTRANS, CDTRANSTYP FROM PL21TRANCODE) code ON ptx.CDTRANS = code.CDTRANS\n" +
        "WHERE aud.NUXREFEM = :empId AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT <= :dateEnd\n" +
        "AND ptx.CDTRANS IN (:transCodes)\n" +
        "ORDER BY ptx.DTEFFECT DESC, ptx.DTTXNORIGIN DESC";

    /** {@inheritDoc} */
    @Override
    public TransactionRecord getLastTransRecord(int empId, TransactionType type) throws TransRecordException {
        return getLastTransRecord(empId, type, getBeginningOfTime(), new Date());
    }

    /** {@inheritDoc} */
    @Override
    public TransactionRecord getLastTransRecord(int empId, TransactionType type, Date end) throws TransRecordException {
        return getLastTransRecord(empId, type, getBeginningOfTime(), end);
    }

    /** {@inheritDoc} */
    @Override
    public TransactionRecord getLastTransRecord(int empId, TransactionType type, Date start, Date end) throws TransRecordException {
        Set<TransactionType> types = new HashSet<>(Arrays.asList(type));
        Map<TransactionType, TransactionRecord> res = getLastTransRecords(empId, types, start, end);
        if (res.size() == 1 && res.containsKey(type)) {
            return res.get(type);
        }
        throw new TransRecordNotFoundEx(
            "No matching transaction of type " + type + " for empId " + empId + " between " + start + " - " + end);
    }

    /** {@inheritDoc} */
    @Override
    public Map<TransactionType, TransactionRecord> getLastTransRecords(int empId, Set<TransactionType> types) {
        return getLastTransRecords(empId, types, new Date());
    }

    /** {@inheritDoc} */
    @Override
    public Map<TransactionType, TransactionRecord> getLastTransRecords(int empId, Set<TransactionType> types, Date end) {
        return getLastTransRecords(empId, types, getBeginningOfTime(), end);
    }

    /** {@inheritDoc} */
    @Override
    public Map<TransactionType, TransactionRecord> getLastTransRecords(int empId, Set<TransactionType> types,
                                                                       Date start, Date end) {
        Map<String, String> selectMap = new HashMap<>();

        /** The set of transaction 'codes' to use in the where clause */
        Set<String> transCodes = new HashSet<>();
        for (TransactionType type : types) {
            transCodes.add(type.name());
        }

        /** The list of column names to be used in the select statement */
        List<String> auditColList = TransactionType.getAllDbColumnsList();
        String auditColumns = StringUtils.join(auditColList, ",");
        selectMap.put("audColumns", (!auditColumns.isEmpty()) ? ("," + auditColumns) : "");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("dateStart", start);
        params.addValue("dateEnd", end);
        params.addValue("transCodes", transCodes);

        StrSubstitutor strSub = new StrSubstitutor(selectMap);
        String sql = strSub.replace(GET_LAST_TRANS_REC_SQL);
        List<TransactionRecord> transRecordList =
                remoteNamedJdbc.query(sql, params, new TransactionRecordRowMapper("", "", types));

        Map<TransactionType, TransactionRecord> transRecordMap = new HashMap<>();
        for (TransactionRecord tr : transRecordList) {
            if (transRecordMap.containsKey(tr.getTransType())) {
                logger.warn("Multiple latest records for a single transaction were returned!");
            }
            transRecordMap.put(tr.getTransType(), tr);
        }
        return transRecordMap;
    }

    /** {@inheritDoc} */
    @Override
    public Map<TransactionType, TransactionHistory> getTransHistoryMap(int empId, Set<TransactionType> types) {
        return getTransHistoryMap(empId, types, new Date());
    }

    /** {@inheritDoc} */
    @Override
    public Map<TransactionType, TransactionHistory> getTransHistoryMap(int empId, Set<TransactionType> types, Date end) {
        return getTransHistoryMap(empId, types, getBeginningOfTime(), end);
    }

    /** {@inheritDoc} */
    @Override
    public Map<TransactionType, TransactionHistory> getTransHistoryMap(int empId, Set<TransactionType> types, Date start, Date end) {
        return null;
    }
}

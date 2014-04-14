package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.exception.TransRecordException;
import gov.nysenate.seta.model.exception.TransRecordNotFoundEx;
import gov.nysenate.seta.model.personnel.TransactionHistory;
import gov.nysenate.seta.model.personnel.TransactionRecord;
import gov.nysenate.seta.model.personnel.TransactionType;
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

    protected static final String GET_TRANS_HISTORY_SQL =
        "SELECT aud.NUXREFEM, ptx.CDSTATUS, ptx.CDTRANS, ptx.CDTRANSTYP, ptx.NUCHANGE, ptx.DTTXNORIGIN, ptx.DTTXNUPDATE,\n" +
        "       ptx.DTEFFECT ${audColumns}\n" +
        "FROM PM21PERAUDIT aud\n" +
        "LEFT JOIN PD21PTXNCODE ptx ON aud.NUCHANGE = ptx.NUCHANGE\n" +
        "LEFT JOIN (SELECT DISTINCT CDTRANS, CDTRANSTYP FROM PL21TRANCODE) code ON ptx.CDTRANS = code.CDTRANS\n" +
        "WHERE aud.NUXREFEM = :empId AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT BETWEEN :dateStart AND :dateEnd\n" +
        "AND ptx.CDTRANS IN (:transCodes)\n" +
        "ORDER BY ptx.DTEFFECT DESC, ptx.DTTXNORIGIN DESC";

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionType> types) {
        return getTransHistory(empId, types, new Date());
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionType> types, Date end) {
        return getTransHistory(empId, types, getBeginningOfTime(), end);
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionType> types, Date start, Date end) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("dateStart", start);
        params.addValue("dateEnd", end);
        params.addValue("transCodes", getTransCodesFromSet(types));

        String sql = applyAuditColumnsInSelectSql(GET_TRANS_HISTORY_SQL, "audColumns", "", types);
        List<TransactionRecord> transRecordList =
                remoteNamedJdbc.query(sql, params, new TransactionRecordRowMapper("", "", types));

        TransactionHistory transHistory = new TransactionHistory(empId);
        transHistory.addTransactionRecords(transRecordList);
        return transHistory;
    }

    /**
     * Helper method to add audit columns to the select sql statement. This is done because the columns need to be
     * explicitly added to prevent name clashes and we don't want to manually write them out.
     * @param selectSql String - The sql with a select statement to add the audit columns to.
     * @param replaceKey String - An identifier inside the sql for replacement. e.g ${auditCols} where 'auditCols' is the key.
     *                            The replacement string cannot be the first entry in the select clause due to commas.
     * @param pfx String - A prefix to apply to each column name. Leave empty if you just want the column name as is.
     * @param restrictSet Set<TransactionType> - Only the columns for the desired types will be added. If null then
     *                                           all the columns for every transaction type will be added.
     * @return String - sql statement with audit columns
     */
    private String applyAuditColumnsInSelectSql(String selectSql, String replaceKey, String pfx, Set<TransactionType> restrictSet) {
        Map<String, String> selectMap = new HashMap<>();

        /** Restrict the columns to just the ones needed unless the set is empty or contains APP or RTP
         *  because those serve as initial snapshots and therefore need all the columns. */
        List<String> auditColList = new ArrayList<>();
        if (restrictSet != null && !restrictSet.isEmpty() && !restrictSet.contains(TransactionType.APP) &&
            !restrictSet.contains(TransactionType.RTP)) {
            for (TransactionType type : restrictSet) {
                auditColList.addAll(type.getDbColumnList());
            }
        }
        else {
            auditColList.addAll(TransactionType.getAllDbColumnsList());
        }

        /** Apply the prefix to the names if requested */
        if (StringUtils.isNotEmpty(pfx)) {
            for (int i = 0; i < auditColList.size(); i++) {
                auditColList.set(i, pfx + auditColList.get(i));
            }
        }

        /** Apply to the sql statement */
        String auditColumns = StringUtils.join(auditColList, ",");
        selectMap.put(replaceKey, (!auditColumns.isEmpty()) ? ("," + auditColumns) : "");
        StrSubstitutor strSub = new StrSubstitutor(selectMap);
        return strSub.replace(selectSql);
    }

    /**
     * Helper method to return a set of the transaction codes in the 'types' set.
     * @param types Set<TransactionType>
     * @return Set<String>
     */
    private Set<String> getTransCodesFromSet(Set<TransactionType> types) {
        Set<String> transCodes = new HashSet<>();
        for (TransactionType type : types) {
            transCodes.add(type.name());
        }
        return transCodes;
    }
}

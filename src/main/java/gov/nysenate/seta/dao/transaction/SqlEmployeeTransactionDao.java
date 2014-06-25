package gov.nysenate.seta.dao.transaction;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.transaction.mapper.TransactionRecordRowMapper;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.model.transaction.TransactionCode;
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
    private boolean earliestRecLikeAppoint = false;

    protected static final String GET_TRANS_HISTORY_SQL =
        "SELECT aud.NUXREFEM, ptx.CDSTATUS, ptx.CDTRANS, ptx.CDTRANSTYP, ptx.NUCHANGE, " +
        "       CAST (ptx.DTTXNORIGIN AS TIMESTAMP) AS DTTXNORIGIN, " +
        "       CAST (ptx.DTTXNUPDATE AS TIMESTAMP) AS DTTXNUPDATE,\n" +
        "       ptx.DTEFFECT ${audColumns}\n" +
        "FROM PM21PERAUDIT aud\n" +
        "LEFT JOIN PD21PTXNCODE ptx ON aud.NUCHANGE = ptx.NUCHANGE\n" +
        "LEFT JOIN (SELECT DISTINCT CDTRANS, CDTRANSTYP FROM PL21TRANCODE) code ON ptx.CDTRANS = code.CDTRANS\n" +
        "WHERE aud.NUXREFEM = :empId AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT BETWEEN :dateStart AND :dateEnd\n" +
        "AND ptx.CDTRANS IN (:transCodes)\n" +
        "ORDER BY ptx.DTEFFECT, ptx.DTTXNORIGIN";

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes) {
        logger.debug(" getTransHistory(int empId, Set<TransactionCode> codes) 1");
        return getTransHistory(empId, codes, new Date());
    }

    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, boolean earliestRecLikeAppoint) {
        logger.debug(" getTransHistory(int empId, Set<TransactionCode> codes, boolean earliestRecLikeAppoint) 2");
        this.earliestRecLikeAppoint = earliestRecLikeAppoint;
        return getTransHistory(empId, codes, new Date(), earliestRecLikeAppoint);
    }

    /** {@inheritDoc} */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date end, boolean earliestRecLikeAppoint) {
        logger.debug(" getTransHistory(int empId, Set<TransactionCode> codes, Date end, boolean earliestRecLikeAppoint) 2");
        this.earliestRecLikeAppoint = earliestRecLikeAppoint;
        return getTransHistory(empId, codes, getBeginningOfTime(), end, earliestRecLikeAppoint);
    }

    /** {@inheritDoc} */
     @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date end) {
        logger.debug(" getTransHistory(int empId, Set<TransactionCode> codes, Date end, boolean earliestRecLikeAppoint) 3");
        return getTransHistory(empId, codes, getBeginningOfTime(), end);
    }

    /** {@inheritDoc} */
    //@Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end) {
        logger.debug(" getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end) 4");
        return getTransHistory(empId, codes, start, end,  false );
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end, boolean earliestRecLikeAppoint) {
        //logger.debug(" getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end, boolean earliestRecLikeAppoint) 5");
        this.earliestRecLikeAppoint = earliestRecLikeAppoint;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("dateStart", start);
        params.addValue("dateEnd", end);

        if (earliestRecLikeAppoint) {
            params.addValue("transCodes", getTransCodesFromSet(null));
        }
        else {
            params.addValue("transCodes", getTransCodesFromSet(codes));
        }

        String sql = applyAuditColumnsInSelectSql(GET_TRANS_HISTORY_SQL, "audColumns", "", codes, earliestRecLikeAppoint);
        List<TransactionRecord> transRecordList =
                remoteNamedJdbc.query(sql, params, new TransactionRecordRowMapper("", "", codes, earliestRecLikeAppoint));

        /*for (int x = 0;x < transRecordList.size();x++) {
            StringBuffer sb = new StringBuffer();
            sb.append(transRecordList.get(x).getValueMap().toString());
            logger.debug("!!!!"+x+":"+sb.toString());
        }*/

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
     * @param restrictSet Set<TransactionCode> - Only the columns for the desired codes will be added. If null then
     *                                           all the columns for every transaction code will be added.
     * @return String - sql statement with audit columns
     */
    private String applyAuditColumnsInSelectSql(String selectSql, String replaceKey, String pfx, Set<TransactionCode> restrictSet, boolean earliestRecLikeAppoint) {
        Map<String, String> selectMap = new HashMap<>();

        /** Restrict the columns to just the ones needed unless the set is empty or contains APP or RTP
         *  because those serve as initial snapshots and therefore need all the columns. */
        List<String> auditColList = new ArrayList<>();
        if (!earliestRecLikeAppoint && restrictSet != null && !restrictSet.isEmpty() && !restrictSet.contains(TransactionCode.APP) &&
            !restrictSet.contains(TransactionCode.RTP)) {
            for (TransactionCode code : restrictSet) {
                auditColList.addAll(code.getDbColumnList());
            }
        }
        else {
            auditColList.addAll(TransactionCode.getAllDbColumnsList());
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
     * Helper method to return a set of the transaction codes in the 'codes' set.
     * @param codes Set<TransactionCode>
     * @return Set<String>
     */
    private Set<String> getTransCodesFromSet(Set<TransactionCode> codes) {
        if (codes==null||codes.size()==0) {
            codes = new HashSet<TransactionCode>();
            TransactionCode[] allTransactionCodes = TransactionCode.values();
            for (TransactionCode curTransCode : allTransactionCodes) {
                codes.add(curTransCode);
            }
        }
        Set<String> transCodes = new HashSet<>();
        for (TransactionCode code : codes) {
            transCodes.add(code.name());
        }
        return transCodes;
    }

}

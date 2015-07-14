package gov.nysenate.seta.dao.transaction;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.transaction.mapper.TransRecordRowMapper;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.util.*;

@Repository
public class OldSqlEmpTransactionDao extends SqlBaseDao implements OldEmpTransactionDao
{
    private static final Logger logger = LoggerFactory.getLogger(OldSqlEmpTransactionDao.class);

    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, LocalDate endDate) {
        return null;
    }

    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, LocalDate endDate, boolean earliestRecLikeAppoint) {
        return null;
    }

    protected static final String GET_TRANS_HISTORY_SQL =
        "SELECT aud.NUXREFEM, ptx.CDSTATUS, ptx.CDTRANS, ptx.CDTRANSTYP, ptx.NUCHANGE, " +
        "       CAST (ptx.DTTXNORIGIN AS TIMESTAMP) AS DTTXNORIGIN, " +
        "       CAST (ptx.DTTXNUPDATE AS TIMESTAMP) AS DTTXNUPDATE,\n" +
        "       ptx.DTEFFECT ${audColumns}\n" +
        "FROM " + MASTER_SCHEMA + ".PM21PERAUDIT aud\n" +
        "JOIN " + MASTER_SCHEMA + ".PD21PTXNCODE ptx ON aud.NUCHANGE = ptx.NUCHANGE\n" +
        "JOIN (SELECT DISTINCT CDTRANS, CDTRANSTYP FROM " + MASTER_SCHEMA + ".PL21TRANCODE) code ON ptx.CDTRANS = code.CDTRANS\n" +
        "WHERE aud.NUXREFEM = :empId AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT BETWEEN :dateStart AND :dateEnd\n" +
        "AND ptx.CDTRANS IN (:transCodes)\n" +
        "ORDER BY ptx.DTEFFECT, ptx.DTTXNORIGIN";

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId) {
        return getTransHistory(empId, TransactionCode.getAll());
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes) {
        return getTransHistory(empId, codes, new Date());
    }

    /** {@inheritDoc} */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, boolean earliestRecLikeAppoint) {
        return getTransHistory(empId, codes, new Date(), earliestRecLikeAppoint);
    }

    /** {@inheritDoc} */
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date end, boolean earliestRecLikeAppoint) {
//        return getTransHistory(empId, codes, getBeginningOfTime(), end, earliestRecLikeAppoint);
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
//     @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date end) {
//        return getTransHistory(empId, codes, getBeginningOfTime(), end);
         throw new NotImplementedException();
     }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end) {
        return getTransHistory(empId, codes, start, end,  false );
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Date start, Date end,
                                              boolean earliestRecLikeAppoint) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("dateStart", start);
        params.addValue("dateEnd", end);

        if (earliestRecLikeAppoint) {
            params.addValue("transCodes", getAllTransCodes());
        }
        else {
            params.addValue("transCodes", getTransCodesFromSet(codes));
        }

        String sql = applyAuditColumnsInSelectSql(GET_TRANS_HISTORY_SQL, "audColumns", "", codes, earliestRecLikeAppoint);
        List<TransactionRecord> transRecordList =
                remoteNamedJdbc.query(sql, params, new TransRecordRowMapper("", "", codes, EmpTransDaoOption.DEFAULT));

        TransactionHistory transHistory = new TransactionHistory(empId, null);
     //    transHistory.addTransactionRecords(transRecordList);
        return transHistory;
    }

    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Range<LocalDate> dateRange) {
        return null;
    }

    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Range<LocalDate> dateRange, boolean requireInitialState) {
        RangeSet<LocalDate> dateRanges = ImmutableRangeSet.of(dateRange);
        return getTransHistory(empId, codes, dateRanges, requireInitialState);
    }

    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, RangeSet<LocalDate> dateRanges, boolean requireInitialState) {
        return null;
    }

    /**
     * Helper method to add audit columns to the select sql statement. This is done because the columns need to be
     * explicitly added to prevent name clashes and we don't want to manually write them out.
     *
     * @param selectSql String - The sql with a select statement to add the audit columns to.
     * @param replaceKey String - An identifier inside the sql for replacement. e.g ${auditCols} where 'auditCols' is the key.
     *                            The replacement string cannot be the first entry in the select clause due to commas.
     * @param pfx String - A prefix to apply to each column name. Leave empty if you just want the column name as is.
     * @param restrictSet Set<TransactionCode> - Only the columns for the desired codes will be added. If null then
     *                                           all the columns for every transaction code will be added.
     * @return String - sql statement with audit columns
     */
    private String applyAuditColumnsInSelectSql(String selectSql, String replaceKey, String pfx,
                                                Set<TransactionCode> restrictSet, boolean earliestRecLikeAppoint) {
        Map<String, String> selectMap = new HashMap<>();

        /** Restrict the columns to just the ones needed unless the set is empty or contains APP or RTP
         *  because those serve as initial snapshots and therefore need all the columns. */
        List<String> auditColList = new ArrayList<>();
        if (!earliestRecLikeAppoint && restrictSet != null && !restrictSet.isEmpty() &&
            !restrictSet.contains(TransactionCode.APP) && !restrictSet.contains(TransactionCode.RTP)) {
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
        if (codes == null) {
            codes = new HashSet<>();
        }
        Set<String> transCodes = new HashSet<>();
        for (TransactionCode code : codes) {
            transCodes.add(code.name());
        }
        return transCodes;
    }

    /**
     * Returns the code strings for all the TransactionCodes.
     * @return Set<String>
     */
    private Set<String> getAllTransCodes() {
        Set<TransactionCode> allCodes = new HashSet<>();
        Collections.addAll(allCodes, TransactionCode.values());
        return getTransCodesFromSet(allCodes);
    }
}

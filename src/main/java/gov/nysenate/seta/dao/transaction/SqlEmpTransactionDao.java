package gov.nysenate.seta.dao.transaction;

import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.transaction.mapper.TransHistoryHandler;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.seta.dao.transaction.SqlEmpTransactionQuery.GET_TRANS_HISTORY_SQL;

@Repository
public class SqlEmpTransactionDao extends SqlBaseDao implements EmpTransactionDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmpTransactionDao.class);

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, EmpTransDaoOption options) {
        return getTransHistory(empId, TransactionCode.getAll(), options);
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, EmpTransDaoOption options) {
        return getTransHistory(empId, codes, Range.all(), options);
    }

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId, Set<TransactionCode> codes, Range<LocalDate> dateRange,
                                              EmpTransDaoOption options) {
        // Use default options if not specified
        options = (options == null) ? EmpTransDaoOption.DEFAULT : options;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId)
              .addValue("dateStart", DateUtils.toDate(DateUtils.startOfDateRange(dateRange)))
              .addValue("dateEnd", DateUtils.toDate(DateUtils.endOfDateRange(dateRange)))
              // We don't filter by transaction codes here if the earliest record needs to be the initial state
              .addValue("transCodes", (!options.shouldInitialize()) ? getTransCodesFromSet(codes) : getAllTransCodes());

        String sql = applyAuditColsToSql(GET_TRANS_HISTORY_SQL.getSql(), "audColumns", "AUD.", codes, options);
        TransHistoryHandler handler = new TransHistoryHandler(empId, "", "", codes, options);
        remoteNamedJdbc.query(sql, params, handler);
        return handler.getTransactionHistory();
    }

    /** --- Internal Methods --- */

    /**
     * Helper method to addUsage audit columns to the select sql statement. This is done because the columns need to be
     * explicitly added to prevent name clashes and we don't want to manually write them out.
     *
     * Note: The replacement string cannot be the first entry in the select clause due to commas.
     *
     * @param selectSql String - The sql with a select statement to addUsage the audit columns to.
     * @param replaceKey String - The key used for replacement. e.g ${auditCols} where 'auditCols' is the key.
     * @param pfx String - A prefix to apply to each column name. Leave empty if you just want the column name as is.
     * @param restrictSet Set<TransactionCode> - Only the columns for the desired codes will be added. If null then
     *                                           all the columns for every transaction code will be added.
     * @return String - sql statement with audit columns applied
     */
    private String applyAuditColsToSql(String selectSql, String replaceKey, String pfx, Set<TransactionCode> restrictSet,
                                       EmpTransDaoOption options) {
        Map<String, String> selectMap = new HashMap<>();

        // Restrict the columns to just the ones needed unless the set is empty or contains APP or RTP
        // because those serve as initial snapshots and therefore need all the columns. */
        List<String> auditColList = new ArrayList<>();
        if (!options.shouldInitialize() && restrictSet != null && !restrictSet.isEmpty() &&
            !restrictSet.stream().anyMatch(TransactionCode::isAppointType)) {
            restrictSet.forEach(t -> auditColList.addAll(t.getDbColumnList()));
        }
        else {
            auditColList.addAll(TransactionCode.getAllDbColumnsList());
        }
        // Apply the prefix to the names if requested */
        if (StringUtils.isNotEmpty(pfx)) {
            for (int i = 0; i < auditColList.size(); i++) {
                auditColList.set(i, pfx + auditColList.get(i));
            }
        }
        // Apply to the sql statement */
        String auditColumns = StringUtils.join(auditColList, ",");
        selectMap.put(replaceKey, (!auditColumns.isEmpty()) ? ("," + auditColumns) : "");
        StrSubstitutor strSub = new StrSubstitutor(selectMap);
        return strSub.replace(selectSql);
    }

    /**
     * Helper method to return a set of the transaction codes in the 'codes' set.
     *
     * @param codes Set<TransactionCode>
     * @return Set<String>
     */
    private Set<String> getTransCodesFromSet(Set<TransactionCode> codes) {
        return codes.stream().map(t -> t.name()).collect(Collectors.toSet());
    }

    /**
     * Returns the code strings for all the TransactionCodes.
     *
     * @return Set<String>
     */
    private Set<String> getAllTransCodes() {
        Set<TransactionCode> allCodes = new HashSet<>();
        Collections.addAll(allCodes, TransactionCode.values());
        return getTransCodesFromSet(allCodes);
    }
}
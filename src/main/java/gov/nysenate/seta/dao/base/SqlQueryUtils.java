package gov.nysenate.seta.dao.base;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.common.LimitOffset;
import gov.nysenate.common.OrderBy;
import gov.nysenate.common.SortOrder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Common utility methods to be used by enums/classes that store sql queries.
 */
public abstract class SqlQueryUtils
{
    /**
     * Wraps the input sql query with a limit offset clause. The returned query will have the
     * proper syntax according to the supplied 'vendor'.
     *
     * @param sql String - The original sql query
     * @param limitOffset LimitOffset - Limit/offset values should be set here
     * @param vendor DbVendor - Used for determining the syntax of the limit clause.
     * @return String
     */
    public static String withLimitOffsetClause(String sql, LimitOffset limitOffset, DbVendor vendor) {
        String limitClause = "";
        if (limitOffset != null) {
            // If the database supports the LIMIT x OFFSET n clause, it's pretty simple.
            if (vendor.supportsLimitOffset()) {
                if (limitOffset.hasLimit()) {
                    limitClause = String.format(" LIMIT %d", limitOffset.getLimit());
                }
                if (limitOffset.hasOffset()) {
                    limitClause += String.format(" OFFSET %d", limitOffset.getOffsetStart());
                }
                return sql + limitClause;
            }
            // Otherwise use ORACLE's subquery approach
            else {
                Integer start = (limitOffset.hasOffset()) ? limitOffset.getOffsetStart() : 1;
                Integer end = (limitOffset.hasLimit()) ? start + limitOffset.getLimit() : 100000;
                return String.format(
                    "SELECT * FROM (SELECT ROWNUM AS rn, q.* FROM (%s) q)\n" +
                    "WHERE rn >= %s AND rn <= %s", sql, start, end);
            }
        }
        return sql;
    }

    /**
     * Wraps the input sql query with an ORDER BY clause that is generated via the given 'orderBy' instance.
     * Ordering of multiple column names is also supported.
     *
     * @param sql String - The original sql query
     * @param orderBy OrderBy - Order by columns and sort orders should be set here.
     * @return String
     */
    public static String withOrderByClause(String sql, OrderBy orderBy) {
        String clause = "";
        if (orderBy != null) {
            ImmutableMap<String, SortOrder> sortColumns = orderBy.getSortColumns();
            List<String> orderClauses = new ArrayList<>();
            for (String column : sortColumns.keySet()) {
                if (!sortColumns.get(column).equals(SortOrder.NONE)) {
                    orderClauses.add(column + " " + sortColumns.get(column).name());
                }
            }
            if (!orderClauses.isEmpty()) {
                clause += "\nORDER BY " + StringUtils.join(orderClauses, ", ");
            }
        }
        return sql + clause;
    }
}
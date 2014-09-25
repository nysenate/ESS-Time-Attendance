package gov.nysenate.seta.dao.base;

public interface BasicSqlQuery
{
    /** TODO: get rid of these. */
    static String MASTER_SCHEMA = "SASS_OWNER";
    static String TS_SCHEMA = "TS_OWNER";

    /**
     * Return the sql query as is.
     */
    public String getSql();

    /**
     * Return which database this query is targeting.
     */
    public DbVendor getVendor();

    /**
     * Returns a sql query that is formatted to support the given limit offset operations.*
     */
    public default String getSql(LimitOffset limitOffset) {
        return SqlQueryUtils.withLimitOffsetClause(getSql(), limitOffset, getVendor());
    }

    /**
     * Returns a sql string with an order by clause set according to the supplied OrderBy instance.
     */
    public default String getSql(OrderBy orderBy) {
        return SqlQueryUtils.withOrderByClause(getSql(), orderBy);
    }

    /**
     * Returns a sql string with a limit offset according to the supplied LimitOffset and an
     * order by clause set according to the supplied OrderBy instance.
     */
    public default String getSql(OrderBy orderBy, LimitOffset limitOffset) {
        return SqlQueryUtils.withLimitOffsetClause(SqlQueryUtils.withOrderByClause(getSql(), orderBy), limitOffset, getVendor());
    }
}
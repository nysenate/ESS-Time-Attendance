package gov.nysenate.seta.dao.base;


import org.springframework.jdbc.object.SqlQuery;

public interface BasicSqlQuery
{
    /**
     * Return the sql query as is.
     */
    public String getSql();

    /**
     * Retrieve a formatted sql String with the envSchema value replaced where
     * applicable. This is needed for allowing configurable schema names.
     */
    public default String getSql(String envSchema) {
        return SqlQueryUtils.getSqlWithSchema(getSql(), envSchema);
    }

    /**
     * Returns a sql string with a limit clause
     * appended to the end according to the supplied LimitOffset instance.
     */
    public default String getSql(LimitOffset limitOffset) {
        return getSql() + SqlQueryUtils.getLimitOffsetClause(limitOffset);
    }

    /**
     * Returns a sql string with an order by clause set according to the supplied OrderBy instance.
     * @param orderBy
     * @return
     */
    public default String getSql(OrderBy orderBy) {
        return getSql() + SqlQueryUtils.getOrderByClause(orderBy);
    }

    /**
     * Returns a sql string with a limit offset according to the supplied LimitOffset and an
     * order by clause set according to the supplied OrderBy instance.
     */
    public default String getSql(OrderBy orderBy, LimitOffset limitOffset) {
        return getSql() + SqlQueryUtils.getOrderByClause(orderBy) + SqlQueryUtils.getLimitOffsetClause(limitOffset);
    }
}
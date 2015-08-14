package gov.nysenate.seta.dao.period;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbSchema;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlHolidayQuery implements BasicSqlQuery
{
    GET_HOLIDAY_SQL(
        "SELECT * FROM " + DbSchema.MASTER_SFMS + ".SASSHD17691 WHERE DTHOLIDAY = :date"
    ),
    GET_HOLIDAYS_SQL(
        "SELECT * FROM " + DbSchema.MASTER_SFMS + ".SASSHD17691 WHERE DTHOLIDAY BETWEEN :startDate AND :endDate"
    ),
    GET_NON_QUESTIONABLE_HOLIDAYS_SQL(
        GET_HOLIDAYS_SQL.sql + " AND cdquest = 'N'"
    );

    private String sql;

    SqlHolidayQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.ORACLE_10g;
    }
}
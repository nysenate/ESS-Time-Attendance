package gov.nysenate.seta.dao.transaction;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlEmpTransactionQuery implements BasicSqlQuery
{
    GET_TRANS_HISTORY_TEMPLATE(
        "SELECT\n" +
        "    AUD.NUXREFEM, AUD.DTTXNORIGIN AS AUD_DTTXNORIGIN, AUD.DTTXNUPDATE AS AUD_DTTXNUPDATE,\n" +
        "    PTX.CDSTATUS, PTX.CDTRANS, PTX.CDTRANSTYP, PTX.NUCHANGE, PTX.NUDOCUMENT," +
        "    PTX.DTTXNORIGIN AS DTTXNORIGIN, PTX.DTTXNUPDATE AS DTTXNUPDATE,\n" +
        "    PTX.DTEFFECT, AUD.DETXNNOTE50, AUD.DETXNNOTEPAY ${audColumns}\n" +
        "FROM ${masterSchema}.PM21PERAUDIT AUD\n" +
        "JOIN ${masterSchema}.PD21PTXNCODE PTX ON AUD.NUCHANGE = PTX.NUCHANGE\n" +
        "JOIN (SELECT DISTINCT CDTRANS, CDTRANSTYP FROM ${masterSchema}.PL21TRANCODE) CD ON PTX.CDTRANS = CD.CDTRANS\n" +
        "%s\n" +
        "ORDER BY PTX.DTEFFECT, PTX.DTTXNORIGIN, AUD.DTTXNORIGIN, AUD.DTTXNUPDATE, PTX.CDTRANS"),

    GET_TRANS_HISTORY_SQL(
        String.format(GET_TRANS_HISTORY_TEMPLATE.sql,
            "WHERE AUD.NUXREFEM = :empId AND PTX.CDSTATUS = 'A' AND PTX.DTEFFECT BETWEEN :dateStart AND :dateEnd\n")
    ),

    GET_TRANS_HISTORY_SQL_FILTER_BY_CODE(
        String.format(GET_TRANS_HISTORY_TEMPLATE.sql, "" +
            "WHERE AUD.NUXREFEM = :empId AND PTX.CDSTATUS = 'A' AND PTX.DTEFFECT BETWEEN :dateStart AND :dateEnd " +
            "AND PTX.CDTRANS IN (:transCodes)")
    ),

    GET_LAST_UPDATED_RECS_SQL(
        String.format(GET_TRANS_HISTORY_TEMPLATE.sql, "WHERE PTX.DTTXNUPDATE > :lastDateTime AND PTX.CDSTATUS = 'A'\n")
    ),

    GET_MAX_UPDATE_DATE_TIME_SQL(
        "SELECT CAST (MAX(PTX.DTTXNUPDATE) AS TIMESTAMP) AS MAX_DTTXNUPDATE\n" +
        "FROM ${masterSchema}.PM21PERAUDIT AUD\n" +
        "JOIN ${masterSchema}.PD21PTXNCODE PTX ON AUD.NUCHANGE = PTX.NUCHANGE"
    )
    ;



    private String sql;

    SqlEmpTransactionQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.ORACLE_10g;
    }
}
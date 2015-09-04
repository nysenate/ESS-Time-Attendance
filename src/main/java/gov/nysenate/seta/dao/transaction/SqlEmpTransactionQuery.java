package gov.nysenate.seta.dao.transaction;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlEmpTransactionQuery implements BasicSqlQuery
{
    GET_TRANS_HISTORY_SQL(
        "SELECT\n" +
        "    AUD.NUXREFEM, AUD.DTTXNORIGIN AS AUD_DTTXNORIGIN, PTX.CDSTATUS, PTX.CDTRANS, PTX.CDTRANSTYP, PTX.NUCHANGE, PTX.NUDOCUMENT," +
        "    CAST (PTX.DTTXNORIGIN AS TIMESTAMP) AS DTTXNORIGIN, CAST (PTX.DTTXNUPDATE AS TIMESTAMP) AS DTTXNUPDATE,\n" +
        "    PTX.DTEFFECT, AUD.DETXNNOTE50, AUD.DETXNNOTEPAY ${audColumns}\n" +
        "FROM ${masterSchema}.PM21PERAUDIT AUD\n" +
        "JOIN ${masterSchema}.PD21PTXNCODE PTX ON AUD.NUCHANGE = PTX.NUCHANGE\n" +
        "JOIN (SELECT DISTINCT CDTRANS, CDTRANSTYP FROM ${masterSchema}.PL21TRANCODE) CD ON PTX.CDTRANS = CD.CDTRANS\n" +
        "WHERE AUD.NUXREFEM = :empId AND PTX.CDSTATUS = 'A' AND PTX.DTEFFECT BETWEEN :dateStart AND :dateEnd\n" +
        "AND PTX.CDTRANS IN (:transCodes)\n" +
        "ORDER BY PTX.DTEFFECT, PTX.DTTXNORIGIN, AUD.DTTXNORIGIN, AUD.DTTXNUPDATE, PTX.CDTRANS");

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
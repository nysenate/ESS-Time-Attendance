package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbVendor;

import static gov.nysenate.seta.dao.base.DbSchema.MASTER_SFMS;
import static gov.nysenate.seta.dao.base.DbSchema.TIMESHEET_SFMS;

public enum SqlSupervisorQuery implements BasicSqlQuery
{
    /**
     * This query returns a listing of all supervisor related transactions for employees that have at
     * one point been assigned the given 'supId'. The results of this query can be processed to determine
     * valid employee groups for a supervisor.
     */
    GET_SUP_EMP_TRANS_SQL(
        "SELECT empList.*, per.NALAST, per.NUXREFSV, per.CDEMPSTATUS, " +
        "       ptx.CDTRANS, ptx.CDTRANSTYP, ptx.DTEFFECT, per.DTTXNORIGIN,\n" +
        "       ROW_NUMBER() " +
        "       OVER (PARTITION BY EMP_GROUP, NUXREFEM, OVR_NUXREFSV ORDER BY DTEFFECT DESC, DTTXNORIGIN DESC) AS TRANS_RANK\n" +
        "FROM (\n" +

        /**  Fetch the ids of the supervisor's direct employees. */
        "    SELECT DISTINCT 'PRIMARY' AS EMP_GROUP, NUXREFEM, NULL AS OVR_NUXREFSV\n" +
        "    FROM " + MASTER_SFMS + ".PM21PERAUDIT WHERE NUXREFSV = :supId \n" +

        /**  Combine that with the ids of the employees that are accessible through the sup overrides.
         *   The EMP_GROUP column will either be 'SUP_OVR' or 'EMP_OVR' to indicate the type of override. */
        "    UNION ALL\n" +
        "    SELECT DISTINCT\n" +
        "    CASE \n" +
        "        WHEN ovr.NUXREFSVSUB IS NOT NULL THEN 'SUP_OVR' \n" +
        "        WHEN ovr.NUXREFEMSUB IS NOT NULL THEN 'EMP_OVR' " +
        "    END,\n" +
        "    per.NUXREFEM, ovr.NUXREFSVSUB\n" +
        "    FROM " + TIMESHEET_SFMS + ".PM23SUPOVRRD ovr\n" +
        "    LEFT JOIN " + MASTER_SFMS + ".PM21PERAUDIT per ON \n" +
        "      CASE WHEN ovr.NUXREFSVSUB IS NOT NULL AND per.NUXREFSV = ovr.NUXREFSVSUB THEN 1\n" +
        "           WHEN ovr.NUXREFEMSUB IS NOT NULL AND per.NUXREFEM = ovr.NUXREFEMSUB THEN 1\n" +
        "           ELSE 0\n" +
        "      END = 1\n" +
        "    WHERE ovr.NUXREFEM = :supId AND ovr.CDSTATUS = 'A'\n" +
        "    AND :endDate BETWEEN NVL(ovr.DTSTART, :endDate) AND NVL(ovr.DTEND, :endDate)\n" +
        "    AND per.NUXREFEM IS NOT NULL\n" +
        "  ) empList\n" +
        "JOIN " + MASTER_SFMS + ".PM21PERAUDIT per ON empList.NUXREFEM = per.NUXREFEM\n" +
        "JOIN " + MASTER_SFMS + ".PD21PTXNCODE ptx ON per.NUXREFEM = ptx.NUXREFEM AND per.NUCHANGE = ptx.NUCHANGE\n" +

        /**  Retrieve just the APP/RTP/SUP/EMP transactions unless the employee doesn't
         *   have any of them (some earlier employees may be missing APP for example). */
        "WHERE \n" +
        "    (per.NUXREFEM NOT IN (SELECT DISTINCT NUXREFEM FROM " + MASTER_SFMS + ".PD21PTXNCODE\n" +
        "                          WHERE CDTRANS IN ('APP', 'RTP', 'SUP'))\n" +
        "    OR ptx.CDTRANS IN ('APP', 'RTP', 'SUP', 'EMP'))\n" +
        "AND ptx.CDTRANSTYP = 'PER'\n" +
        "AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT <= :endDate\n" +
        "ORDER BY NUXREFEM, TRANS_RANK"),

    GET_SUP_CHAIN_EXCEPTIONS(
        "SELECT NUXREFEM, NUXREFSV, CDTYPE, CDSTATUS FROM " + MASTER_SFMS + ".PM23SPCHNEX\n" +
        "WHERE CDSTATUS = 'A' AND NUXREFEM = :empId")
    ;

    private String sql;

    SqlSupervisorQuery(String sql) {
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
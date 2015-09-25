package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlEmployeeQuery implements BasicSqlQuery
{
    GET_EMP_SQL_TMPL(
        "SELECT DISTINCT \n" +
         // Personal details
        "per.*, ttl.FFDEEMPTITLL, addr.ADSTREET1, addr.ADSTREET2, addr.ADCITY, addr.ADSTATE, addr.ADZIPCODE,\n" +
         // Responsibility center
        "rctr.DTEFFECTBEG AS RCTR_DTEFFECTBEG, rctr.DTEFFECTEND AS RCTR_DTEFFECTEND,\n" +
        "rctr.CDSTATUS AS RCTR_CDSTATUS, rctr.CDRESPCTR AS RCTR_CDRESPCTR,\n" +
        "rctr.DERESPCTR AS RCTR_DERESPCTR, \n" +
         // Responsibility center head
        "rctrhd.CDRESPCTRHD AS RCTRHD_CDRESPCTRHD, rctrhd.CDSTATUS AS RCTRHD_CDSTATUS, " +
        "rctrhd.CDAFFILIATE AS RCTRHD_CDAFFILIATE, rctrhd.DERESPCTRHDS AS RCTRHD_DERESPCTRHDS, \n" +
        "rctrhd.FFDERESPCTRHDF AS RCTRHD_FFDERESPCTRHDF,\n" +
         // Agency
        "agcy.CDAGENCY AS AGCY_CDAGENCY, agcy.CDSTATUS AS AGCY_CDSTATUS,\n" +
        "agcy.DEAGENCYS AS AGCY_DEAGENCYS, agcy.DEAGENCYF AS AGCY_DEAGENCYF,\n" +
         // Work location
        "loc.CDLOCAT AS LOC_CDLOCAT, loc.CDLOCTYPE AS LOC_CDLOCTYPE,\n" +
        "loc.FFADSTREET1 AS LOC_FFADSTREET1, loc.FFADSTREET2 AS LOC_FFADSTREET2,\n" +
        "loc.FFADCITY AS LOC_FFADCITY, loc.ADSTATE AS LOC_ADSTATE,\n" +
        "loc.ADZIPCODE AS LOC_ADZIPCODE\n" +

        "FROM ${masterSchema}.PM21PERSONN per\n" +
        "LEFT JOIN ${masterSchema}.PL21EMPTITLE ttl ON per.CDEMPTITLE = ttl.CDEMPTITLE\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.PM21ADDRESS WHERE CDADDRTYPE = 'LEGL') addr ON per.NUXREFEM = addr.NUXREFEM\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.SL16RESPCTR WHERE CDSTATUS = 'A') rctr ON per.CDRESPCTR = rctr.CDRESPCTR AND per.CDAGENCY = rctr.CDAGENCY\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.SL16RSPCTRHD WHERE CDSTATUS = 'A') rctrhd ON rctr.CDRESPCTRHD = rctrhd.CDRESPCTRHD\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.SL16AGENCY WHERE CDSTATUS = 'A') agcy ON rctr.CDAGENCY = agcy.CDAGENCY\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.SL16LOCATION WHERE CDSTATUS = 'A') loc ON per.CDLOCAT = loc.CDLOCAT\n" +
        "WHERE %s \n"),

    GET_EMP_BY_ID_SQL(String.format(GET_EMP_SQL_TMPL.sql, "per.NUXREFEM = :empId")),
    GET_EMP_BY_EMAIL_SQL(String.format(GET_EMP_SQL_TMPL.sql, "per.NAEMAIL = :email")),
    GET_ACTIVE_EMPS_SQL(String.format(GET_EMP_SQL_TMPL.sql, "per.CDEMPSTATUS = 'A'")),
    GET_EMPS_BY_IDS_SQL(String.format(GET_EMP_SQL_TMPL.sql, "per.NUXREFEM IN :empIdSet")),

    GET_ACTIVE_EMP_IDS(
        "SELECT DISTINCT NUXREFEM\n" +
        "FROM ${masterSchema}.PM23ATTEND\n" +
        "WHERE CDSTATUS = 'A' AND (DTCLOSE IS NULL OR DTCLOSE > SYSDATE)"
    ),
    ;

    private String sql;

    SqlEmployeeQuery(String sql) {
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

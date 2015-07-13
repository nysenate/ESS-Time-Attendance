package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbSchema;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlTimeRecordQuery implements BasicSqlQuery
{
    GET_TIME_REC_SQL_TEMPLATE(
        "SELECT \n" +
        /**   PM23TIMESHEET columns (no alias needed) */
        "    rec.NUXRTIMESHEET, rec.NUXREFEM, rec.NATXNORGUSER, rec.NATXNUPDUSER, rec.NAUSER, rec.DTTXNORIGIN, rec.DTTXNUPDATE, " +
        "    rec.CDSTATUS, rec.CDTSSTAT, rec.DTBEGIN, rec.DTEND, rec.DEREMARKS, rec.NUXREFSV, rec.DEEXCEPTION, " +
        "    rec.DTPROCESS, " +
        /**   PD23TIMESHEET columns (aliased with ENT_) */
        "    ent.NUXRDAY AS ENT_NUXRDAY, ent.NUXRTIMESHEET, ent.NUXRTIMESHEET AS ENT_NUXRTIMESHEET, ent.NUXREFEM AS ENT_NUXREFEM, " +
        "    ent.DTDAY AS ENT_DTDAY, ent.NUWORK AS ENT_NUWORK, ent.NUTRAVEL AS ENT_NUTRAVEL, ent.NUHOLIDAY AS ENT_NUHOLIDAY, " +
        "    ent.NUVACATION AS ENT_NUVACATION, ent.NAUSER AS ENT_NAUSER, ent.NUPERSONAL AS ENT_NUPERSONAL, ent.NUSICKEMP AS ENT_NUSICKEMP, " +
        "    ent.NUSICKFAM AS ENT_NUSICKFAM, ent.NUMISC AS ENT_NUMISC, ent.NUXRMISC AS ENT_NUXRMISC, ent.NATXNORGUSER AS ENT_NATXNORGUSER," +
        "    ent.NATXNUPDUSER AS ENT_NATXNUPDUSER, ent.DTTXNORIGIN AS ENT_DTTXNORIGIN, ent.DTTXNUPDATE AS ENT_DTTXNUPDATE, " +
        "    ent.CDSTATUS AS ENT_CDSTATUS, ent.DECOMMENTS AS ENT_DECOMMENTS, ent.CDPAYTYPE AS ENT_CDPAYTYPE " +
        "FROM " + DbSchema.TIMESHEET_SFMS + ".PM23TIMESHEET rec " +
        "LEFT JOIN " + DbSchema.TIMESHEET_SFMS + ".PD23TIMESHEET ent ON rec.NUXRTIMESHEET = ent.NUXRTIMESHEET \n" +
        "WHERE rec.CDSTATUS = 'A' AND ent.CDSTATUS = 'A' %s" + "\n" +
        "ORDER BY rec.NUXREFEM ASC, rec.DTBEGIN ASC, ent.DTDAY ASC"
    ),
    GET_TIME_REC_BY_DATES(
        String.format(GET_TIME_REC_SQL_TEMPLATE.getSql(),
                "AND rec.NUXREFEM IN (:empIds) AND (:startDate <= TRUNC(rec.DTBEGIN)) AND (:endDate >= TRUNC(rec.DTEND)) " +
                        "AND rec.CDTSSTAT IN (:statuses)")
    ),
    GET_TREC_BY_EMPID(
        String.format(GET_TIME_REC_SQL_TEMPLATE.getSql(),
                "NUXREFEM = :empId")
    ),
    GET_TREC_BY_DATE(
        String.format(GET_TIME_REC_SQL_TEMPLATE.getSql(),
        "DTBEGIN = :startDate AND DTEND = :endDate")
    ),
    GET_TREC_BY_RECSTATUS(
        String.format(GET_TIME_REC_SQL_TEMPLATE.getSql(),
        "CDTSSTAT = :tSStatusId AND NUXREFEM = :empId AND DTBEGIN = :startDate AND DTEND = :endDate")
    ),
    INSERT_TIME_REC(
        "INSERT \n" +
        "INTO " + DbSchema.TIMESHEET_SFMS + ".PM23TIMESHEET \n" +
        "(NUXRTIMESHEET, NUXREFEM, NATXNORGUSER, NATXNUPDUSER, NAUSER, DTTXNORIGIN, DTTXNUPDATE, CDSTATUS," +
        " CDTSSTAT, DTBEGIN, DTEND, DEREMARKS, NUXREFSV, DEEXCEPTION, DTPROCESS) \n" +
        "VALUES (:timesheetId,  :empId, :tOriginalUserId, :tUpdateUserId, :employeeName, :tOriginalDate, :tUpdateDate, :status, " +
                ":tSStatusId, :beginDate, :endDate, :remarks, :supervisorId, :excDetails, :procDate) \n"
    ),
    UPDATE_TIME_REC_SQL (
        "UPDATE " + DbSchema.TIMESHEET_SFMS + ".PM23TIMESHEET \n" +
        "SET \n" +
        "  NUXREFEM = :empId, NATXNORGUSER = :tOriginalUserId, NATXNUPDUSER = :tUpdateUserId, " +
        "  DTTXNORIGIN = :tOriginalDate, DTTXNUPDATE = :tUpdateDate, CDSTATUS = :status, CDTSSTAT = :tSStatusId, " +
        "  DTBEGIN = :beginDate, DTEND = :endDate, DEREMARKS = :remarks, NUXREFSV = :supervisorId, " +
        "  DEEXCEPTION = :excDetails, DTPROCESS = :procDate, NAUSER = :employeeName \n" +
        "WHERE NUXRTIMESHEET = :timesheetId"
    )
    ;

    private String sql;

    SqlTimeRecordQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.ORACLE;
    }
}

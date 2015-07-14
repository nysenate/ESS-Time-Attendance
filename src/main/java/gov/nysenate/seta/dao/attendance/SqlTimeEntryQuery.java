package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbSchema;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlTimeEntryQuery implements BasicSqlQuery
{
    SELECT_TIME_ENTRY_BY_TIME_ENTRY_ID(
        "SELECT * FROM " + DbSchema.TIMESHEET_SFMS + ".PD23TIMESHEET\n" +
        "WHERE CDSTATUS = :status AND NUXRDAY = :tSDayId "
    ),
    SELECT_TIME_ENTRIES_BY_TIME_RECORD_ID(
        "SELECT * FROM " + DbSchema.TIMESHEET_SFMS + ".PD23TIMESHEET\n" +
        "WHERE CDSTATUS = :status AND NUXRTIMESHEET = :timesheetId "
    ),
    INSERT_TIME_ENTRY(
        "INSERT INTO " + DbSchema.TIMESHEET_SFMS + ".PD23TIMESHEET\n" +
        " (NUXRDAY, NUXRTIMESHEET, NUXREFEM, NAUSER, DTDAY, NUWORK, NUTRAVEL, NUHOLIDAY, NUSICKEMP, " +
        "  NUSICKFAM, NUMISC, NUXRMISC, NATXNORGUSER, NATXNUPDUSER, DTTXNORIGIN, DTTXNUPDATE, " +
        "  CDSTATUS, DECOMMENTS, CDPAYTYPE, NUVACATION, NUPERSONAL)\n" +
        "VALUES (:tSDayId, :timesheetId, :empId, :employeeName, :dayDate, :workHR, :travelHR, :holidayHR, :sickEmpHR, :sickFamilyHR, " +
        "        :miscHR, :miscTypeId, :tOriginalUserId, :tUpdateUserId, :tOriginalDate, :tUpdateDate, :status, " +
        "        :empComment, :payType, :vacationHR, :personalHR )"
    ),
    UPDATE_TIME_ENTRY(
        "UPDATE " + DbSchema.TIMESHEET_SFMS + ".PD23TIMESHEET " + "\n" +
        "SET NUXRTIMESHEET = :timesheetId, NAUSER = :employeeName, NUWORK = :workHR, NUTRAVEL = :travelHR, " +
            "NUHOLIDAY = :holidayHR, NUSICKEMP = :sickEmpHR, NUSICKFAM = :sickFamilyHR, NUMISC = :miscHR, " +
            "NUXRMISC = :miscTypeId, NATXNORGUSER = :tOriginalUserId, NATXNUPDUSER = :tUpdateUserId, " +
            "DTTXNORIGIN = :tOriginalDate, DTTXNUPDATE = :tUpdateDate, CDSTATUS = :status, DECOMMENTS = :empComment, " +
            "CDPAYTYPE = :payType, NUVACATION = :vacationHR, NUPERSONAL = :personalHR " + "\n" +
        "WHERE NUXREFEM = :empId AND DTDAY = :dayDate"
    );

    private String sql;

    SqlTimeEntryQuery(String sql) {
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

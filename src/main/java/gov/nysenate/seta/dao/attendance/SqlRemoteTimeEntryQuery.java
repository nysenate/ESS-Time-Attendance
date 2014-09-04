package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BasicSqlQuery;

public enum SqlRemoteTimeEntryQuery implements BasicSqlQuery{
    SELECT_TIME_ENTRY_BY_TIME_RECORD_ID(
        "SELECT * FROM PD23TIMESHEET " + "\n" +
        "WHERE CDSTATUS = :status AND NUXRTIMESHEET = :timesheetId "
    ),
    INSERT_TIME_ENTRY_SQL(
        "INSERT INTO PD23TIMESHEET ( NUXRDAY, NUXRTIMESHEET, NUXREFEM, NAUSER, DTDAY, NUWORK, NUTRAVEL, NUHOLIDAY, NUSICKEMP, " +
        "                           NUSICKFAM, NUMISC, NUXRMISC, NATXNORGUSER, NATXNUPDUSER, DTTXNORIGIN, DTTXNUPDATE, " +
        "                           CDSTATUS, DECOMMENTS, CDPAYTYPE, NUVACATION, NUPERSONAL ) "+ "\n" +
        "VALUES ( :tSDayId, :timesheetId, :empId, :employeeName, :dayDate, :workHR, :travelHR, :holidayHR, :sickEmpHR, :sickFamilyHR, " +
        "        :miscHR, :miscTypeId, :tOriginalUserId, :tUpdateUserId, :tOriginalDate, :tUpdateDate, :status, " +
        "        :empComment, :payType, :vacationHR, :personalHR )"
    ),
    UPDATE_TIME_ENTRY_SQL(
        "UPDATE PD23TIMESHEET " + "\n" +
        "SET NUXRTIMESHEET = :timesheetId, NUXREFEM = :empId, NAUSER = :employeeName, DTDAY = :dayDate, NUWORK = :workHR, NUTRAVEL = :travelHR, " +
            "NUHOLIDAY = :holidayHR, NUSICKEMP = :sickEmpHR, NUSICKFAM = :sickFamilyHR, NUMISC = :miscHR, " +
            "NUXRMISC = :miscTypeId, NATXNORGUSER = :tOriginalUserId, NATXNUPDUSER = :tUpdateUserId, " +
            "DTTXNORIGIN = :tOriginalDate, DTTXNUPDATE = :tUpdateDate, CDSTATUS = :status, DECOMMENTS = :empComment, " +
            "CDPAYTYPE = :payType, NUVACATION = :vacationHR, NUPERSONAL = :personalHR " + "\n" +
        "WHERE NUXRDAY = :tSDayId"
    )
    ;

    private String sql;

    private SqlRemoteTimeEntryQuery(String sql){
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}

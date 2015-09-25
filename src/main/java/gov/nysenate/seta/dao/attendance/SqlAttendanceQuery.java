package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlAttendanceQuery implements BasicSqlQuery {
    GET_OPEN_ATTENDANCE_YEARS(
        "SELECT DTPERIODYEAR\n" +
        "FROM ${masterSchema}.PM23ATTEND\n" +
        "WHERE CDSTATUS = 'A' AND NUXREFEM = :empId AND (DTCLOSE IS NULL OR DTCLOSE > SYSDATE)\n" +
        "ORDER BY DTPERIODYEAR ASC"
    )
    ;

    private String sql;

    SqlAttendanceQuery(String sql) {
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

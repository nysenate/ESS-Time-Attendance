package gov.nysenate.seta.dao.payroll;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlPaycheckQuery implements BasicSqlQuery
{
    GET_EMPLOYEE_PAYCHECKS_BY_YEAR(
            "SELECT NUXREFEM, MONET, MOGROSS, MOCHECKAMT, MOADVICEAMT, NUPERIOD, CDAGENCY, NULINE, DTCHECK\n" +
            "from ${masterSchema}.PM25SALLEDG where NUXREFEM = :empId and EXTRACT(YEAR FROM DTCHECK) = :year"
    );

    private String sql;

    SqlPaycheckQuery(String sql) {
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

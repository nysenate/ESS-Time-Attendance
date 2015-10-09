package gov.nysenate.seta.dao.payroll;

import gov.nysenate.seta.dao.base.BasicSqlQuery;
import gov.nysenate.seta.dao.base.DbVendor;

public enum SqlDeductionDaoQuery implements BasicSqlQuery
{
    GET_PAYCHECK_DEDUCTIONS(
            "SELECT l.CDDEDUCTION, l.DEDEDUCTIONF, d.MODEDUCTION FROM ${masterSchema}.pl25deductcd l\n" +
            "JOIN ${masterSchema}.pd25salledg d ON d.CDDEDUCTION = l.CDDEDUCTION\n" +
            "WHERE d.NUXREFEM = :empId AND d.DTCHECK = :checkDate AND d.CDSTATUS = 'A'"
    );

    private String sql;

    SqlDeductionDaoQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return null;
    }
}

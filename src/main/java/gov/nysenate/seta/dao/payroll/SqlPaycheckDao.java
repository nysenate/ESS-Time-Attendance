package gov.nysenate.seta.dao.payroll;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.payroll.mapper.PaycheckHandler;
import gov.nysenate.seta.model.payroll.Paycheck;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gov.nysenate.seta.dao.payroll.SqlPaycheckQuery.GET_EMPLOYEE_PAYCHECKS_BY_YEAR;

@Repository
public class SqlPaycheckDao extends SqlBaseDao implements PayCheckDao
{
    /** {@inheritDoc} */
    @Override
    public List<Paycheck> getEmployeePaychecksForYear(int empId, int year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId)
              .addValue("year", year);
        String sql = GET_EMPLOYEE_PAYCHECKS_BY_YEAR.getSql(schemaMap());
        PaycheckHandler handler = new PaycheckHandler();
        remoteNamedJdbc.query(sql, params, handler);
        return handler.getPaychecks();
    }
}

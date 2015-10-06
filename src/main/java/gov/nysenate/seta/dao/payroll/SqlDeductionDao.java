package gov.nysenate.seta.dao.payroll;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.payroll.mapper.DeductionRowMapper;
import gov.nysenate.seta.model.payroll.Deduction;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SqlDeductionDao extends SqlBaseDao implements DeductionDao {

    @Override
    public List<Deduction> getDeductionsForPaycheck(int empId, LocalDate checkDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId)
              .addValue("checkDate", toDate(checkDate));
        String sql = SqlDeductionDaoQuery.GET_PAYCHECK_DEDUCTIONS.getSql(schemaMap());
        return remoteNamedJdbc.query(sql, params, new DeductionRowMapper());
    }

}

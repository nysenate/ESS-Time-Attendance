package gov.nysenate.seta.dao.personnel;

import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.personnel.mapper.EmployeeRowMapper;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeException;
import gov.nysenate.seta.model.personnel.EmployeeNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.seta.dao.personnel.SqlEmployeeQuery.*;

@Repository
public class SqlEmployeeDao extends SqlBaseDao implements EmployeeDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDao.class);

    /** {@inheritDoc} */
    @Override
    public Employee getEmployeeById(int empId) throws EmployeeException {
        Employee employee;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        try {
            employee = remoteNamedJdbc.queryForObject(GET_EMP_BY_ID_SQL.getSql(schemaMap()), params, getEmployeeRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            logger.warn("Retrieve employee {} error: {}", empId, ex.getMessage());
            throw new EmployeeNotFoundEx("No matching employee record for employee id: " + empId);
        }
        return employee;
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, Employee> getEmployeesByIds(List<Integer> empIds) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empIdSet", new HashSet<>(empIds));
        return getEmployeeMap(GET_EMPS_BY_IDS_SQL.getSql(schemaMap()), params);
    }

    /** {@inheritDoc} */
    @Override
    public Employee getEmployeeByEmail(String email) throws EmployeeException {
        Employee employee;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        try {
            employee = remoteNamedJdbc.queryForObject(GET_EMP_BY_EMAIL_SQL.getSql(schemaMap()), params, getEmployeeRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            throw new EmployeeNotFoundEx("No matching employee record for email: " + email);
        }
        return employee;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Integer> getActiveEmployeeIds(LocalDate startDate) {
        MapSqlParameterSource params = new MapSqlParameterSource("startDate", DateUtils.toDate(startDate));
        return new HashSet<>(remoteNamedJdbc.query(GET_ACTIVE_EMP_IDS_AFTER_DATE_SQL.getSql(schemaMap()), params,
                (rs, rowNum) -> rs.getInt("NUXREFEM")));
    }

    /**
     * Helper method to create employee id -> Employee object mappings.
     * @param sql String - The sql query to execute
     * @param params MapSqlParameterSource - The parameters to supply to the sql query.
     * @return Map(Integer, Employee)
     */
    private Map<Integer, Employee> getEmployeeMap(String sql, MapSqlParameterSource params) {
        Map<Integer, Employee> employeeMap = new LinkedHashMap<>();
        List<Employee> employees = remoteNamedJdbc.query(sql, params, getEmployeeRowMapper());
        for (Employee emp : employees) {
            employeeMap.put(emp.getEmployeeId(), emp);
        }
        return employeeMap;
    }

    /** Returns a EmployeeRowMapper that's configured for use in this dao */
    private static EmployeeRowMapper getEmployeeRowMapper() {
        return new EmployeeRowMapper("", "RCTR_", "RCTRHD_", "AGCY_", "LOC_");
    }
}

package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Supervisor;
import gov.nysenate.seta.model.SupervisorException;
import gov.nysenate.seta.model.SupervisorNotFoundEx;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Repository
public class SqlSupervisorDao extends SqlBaseDao implements SupervisorDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDao.class);

    @Autowired
    private EmployeeDao employeeDao;

    protected static final String IS_EMP_ID_CURR_SUPERVISOR_SQL =
        "SELECT DISTINCT 1 FROM PM21PERSONN WHERE NUXREFSV = :empId";

    protected static final String IS_EMP_ID_SUPERVISOR_DURING_DATE_SQL =
        "SELECT DISTINCT 1 FROM PV23TASUP WHERE NUXREFSV = :empId AND (:endDate BETWEEN DTBEGINSPLIT AND DTENDSPLIT)";

    protected static final String GET_SUPERVISOR_BY_ID_SQL =
        "SELECT NUXREFSV FROM PV23TASUP \n" +
        "WHERE NUXREFEM = :empId AND (:endDate BETWEEN DTBEGINSPLIT AND DTENDSPLIT)";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupervisor(int empId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        try {
            remoteNamedJdbc.queryForObject(IS_EMP_ID_CURR_SUPERVISOR_SQL, params, Integer.class);
            return true;
        }
        catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupervisor(int empId, Date date) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("endDate", date);
        try {
            remoteNamedJdbc.queryForObject(IS_EMP_ID_SUPERVISOR_DURING_DATE_SQL, params, Integer.class);
            return true;
        }
        catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSupervisorIdForEmp(int empId) throws SupervisorException {
        DateTime now = DateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("endDate", now.toDate());
        try {
            return remoteNamedJdbc.queryForObject(GET_SUPERVISOR_BY_ID_SQL, params, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("NUXREFSV");
                }
            });
        }
        catch (IncorrectResultSizeDataAccessException ex) {
            logger.warn("Retrieve supervisor id for emp id: {} with end date: {} error: {}", empId, now.toString(), ex);
            throw new SupervisorNotFoundEx();
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSupervisorIdForEmp(int empId, Date date) throws SupervisorException {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supervisor getSupervisor(int supId) throws SupervisorException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supervisor getSupervisor(int supId, Date date) throws SupervisorException {
        return null;
    }
}

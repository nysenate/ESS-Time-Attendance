package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Employee;
import gov.nysenate.seta.model.EmployeeNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class SqlEmployeeDao extends SqlBaseDao implements EmployeeDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDao.class);

    private static final String GET_EMP_SQL_TMPL =
        "SELECT " +
            "per.*,\n" +

            "rctr.DTEFFECTBEG AS RCTR_DTEFFECTBEG, rctr.DTEFFECTEND AS RCTR_DTEFFECTEND,\n" +
            "rctr.CDSTATUS AS RCTR_CDSTATUS, rctr.CDRESPCTR AS RCTR_CDRESPCTR,\n" +
            "rctr.DERESPCTR AS RCTR_DERESPCTR, \n" +

            "rctrhd.CDRESPCTRHD AS RCTRHD_CDRESPCTRHD, rctrhd.CDSTATUS AS RCTRHD_CDSTATUS, " +
            "rctrhd.CDAFFILIATE AS RCTRHD_CDAFFILIATE, rctrhd.DERESPCTRHDS AS RCTRHD_DERESPCTRHDS, \n" +
            "rctrhd.FFDERESPCTRHDF AS RCTRHD_FFDERESPCTRHDF,\n" +

            "agcy.CDAGENCY AS AGCY_CDAGENCY, agcy.CDSTATUS AS AGCY_CDSTATUS,\n" +
            "agcy.DEAGENCYS AS AGCY_DEAGENCYS, agcy.DEAGENCYF AS AGCY_DEAGENCYF,\n" +

            "loc.CDLOCAT AS LOC_CDLOCAT, loc.CDLOCTYPE AS LOC_CDLOCTYPE,\n" +
            "loc.FFADSTREET1 AS LOC_FFADSTREET1, loc.FFADSTREET2 AS LOC_FFADSTREET2,\n" +
            "loc.FFADCITY AS LOC_FFADCITY, loc.ADSTATE AS LOC_ADSTATE,\n" +
            "loc.ADZIPCODE AS LOC_ADZIPCODE\n" +

        "FROM PM21PERSONN per\n" +
        "LEFT JOIN SL16RESPCTR rctr ON per.CDRESPCTR = rctr.CDRESPCTR\n" +
        "LEFT JOIN SL16RSPCTRHD rctrhd ON rctr.CDRESPCTRHD = rctrhd.CDRESPCTRHD\n" +
        "LEFT JOIN SL16AGENCY agcy ON rctr.CDAGENCY = agcy.CDAGENCY\n" +
        "LEFT JOIN SL16LOCATION loc ON per.CDLOCAT = loc.CDLOCAT\n" +
        "WHERE %s \n" +
        "AND rctr.CDSTATUS = 'A' AND rctrhd.CDSTATUS = 'A' AND per.CDAGENCY = rctr.CDAGENCY \n" +
        "AND loc.CDSTATUS = 'A'";

    private static final String GET_EMP_BY_ID_SQL = String.format(GET_EMP_SQL_TMPL, "per.NUXREFEM = :empId");
    private static final String GET_EMP_BY_EMAIL_SQL = String.format(GET_EMP_SQL_TMPL, "per.NAEMAIL = :email");

    /**
     * {@inheritDoc}
     */
    @Override
    public Employee getEmployeeById(int empId) throws EmployeeNotFoundEx {
        Employee employee;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        try {
            employee = remoteNamedJdbc.queryForObject(GET_EMP_BY_ID_SQL, params,
                    new EmployeeRowMapper("", "RCTR_", "RCTRHD_", "AGCY_", "LOC_"));
        }
        catch (DataRetrievalFailureException ex) {
            throw new EmployeeNotFoundEx("No matching record for employee id: " + empId);
        }
        return employee;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Employee getEmployeeByEmail(String uid) throws EmployeeNotFoundEx {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Employee> getEmployees() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Employee> getEmployeeIdMap() {
        return null;
    }
}

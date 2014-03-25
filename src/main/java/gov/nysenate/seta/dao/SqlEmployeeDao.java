package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;

import static gov.nysenate.seta.model.TransactionType.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SqlEmployeeDao extends SqlBaseDao implements EmployeeDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDao.class);

    @Autowired
    protected EmployeeTransactionDao transHistoryDao;

    protected static final String GET_EMP_SQL_TMPL =
        "SELECT DISTINCT \n" +
            "per.*, ttl.FFDEEMPTITLL, addr.ADSTREET1, addr.ADSTREET2, addr.ADCITY, addr.ADSTATE, addr.ADZIPCODE,\n" +

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
        "LEFT JOIN PL21EMPTITLE ttl ON per.CDEMPTITLE = ttl.CDEMPTITLE\n" +
        "LEFT JOIN (SELECT * FROM PM21ADDRESS WHERE CDADDRTYPE = 'LEGL') addr ON per.NUXREFEM = addr.NUXREFEM\n" +
        "LEFT JOIN (SELECT * FROM SL16RESPCTR WHERE CDSTATUS = 'A') rctr ON per.CDRESPCTR = rctr.CDRESPCTR AND per.CDAGENCY = rctr.CDAGENCY\n" +
        "LEFT JOIN (SELECT * FROM SL16RSPCTRHD WHERE CDSTATUS = 'A') rctrhd ON rctr.CDRESPCTRHD = rctrhd.CDRESPCTRHD\n" +
        "LEFT JOIN (SELECT * FROM SL16AGENCY WHERE CDSTATUS = 'A') agcy ON rctr.CDAGENCY = agcy.CDAGENCY\n" +
        "LEFT JOIN (SELECT * FROM SL16LOCATION WHERE CDSTATUS = 'A') loc ON per.CDLOCAT = loc.CDLOCAT\n" +
        "WHERE %s \n";

    protected static final String GET_EMP_BY_ID_SQL = String.format(GET_EMP_SQL_TMPL, "per.NUXREFEM = :empId");
    protected static final String GET_EMP_BY_EMAIL_SQL = String.format(GET_EMP_SQL_TMPL, "per.NAEMAIL = :email");
    protected static final String GET_ACTIVE_EMPS_SQL = String.format(GET_EMP_SQL_TMPL, "per.CDEMPSTATUS = 'A'");

    /** Set of TransactionTypes that involve data used to construct an Employee. */
    protected static Set<TransactionType> effectiveTransTypes = new HashSet<>();
    static {
        effectiveTransTypes.addAll(
                Arrays.asList(APP, AGY, EMP, LEG, LOC, MAR, NAM, NAT, PHO, RTP, SEX, SUP, TLE, TYP));
    }

    /** {@inheritDoc} */
    @Cacheable(value = "employees")
    @Override
    public Employee getEmployeeById(int empId) throws EmployeeException {
        Employee employee;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        try {
            employee = remoteNamedJdbc.queryForObject(GET_EMP_BY_ID_SQL, params,
                    new EmployeeRowMapper("", "RCTR_", "RCTRHD_", "AGCY_", "LOC_"));
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
        return null;
    }

    @Override
    public List<Integer> getActiveEmployeeIds() {
        return null;
    }

    @Override
    public List<Integer> getActiveEmployeesDuring(Date start, Date end) {
        return null;
    }

    @Override
    public Map<Integer, Employee> getActiveEmployeeMap(Date start, Date end) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Employee getEmployeeByEmail(String email) throws EmployeeException {
        Employee employee;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        try {
            employee = remoteNamedJdbc.queryForObject(GET_EMP_BY_EMAIL_SQL, params,
                    new EmployeeRowMapper("", "RCTR_", "RCTRHD_", "AGCY_", "LOC_"));
        }
        catch (DataRetrievalFailureException ex) {
            throw new EmployeeNotFoundEx("No matching employee record for email: " + email);
        }
        return employee;
    }

    /** {@inheritDoc} */
    //@Override
    public List<Employee> getActiveEmployees() {
        return remoteNamedJdbc.query(GET_ACTIVE_EMPS_SQL,
                new EmployeeRowMapper("", "RCTR_", "RCTRHD_", "AGCY_", "LOC_"));
    }

    /** {@inheritDoc} */
    //@Override
    public Map<Integer, Employee> getActiveEmployeeMap() {
        List<Employee> activeEmployees = getActiveEmployees();
        Map<Integer, Employee> actEmpMap = new ConcurrentHashMap<>();
        for (Employee emp : activeEmployees) {
            actEmpMap.put(emp.getEmployeeId(), emp);
        }
        return actEmpMap;
    }

    /**
     *
     * @param emp Employee -
     * @param latestTransMap Map(TransactionType, TransactionRecord) -
     * @return Employee
     */
    protected Employee getEmployeeSnapshot(Employee emp, Map<TransactionType, TransactionRecord> latestTransMap) {
        if (emp != null) {
            for (TransactionType type : latestTransMap.keySet()) {
                Map<String, String> valueMap = latestTransMap.get(type).getValueMap();
                switch (type) {
                    case AGY : { /** Responsibility center. */
                        break;
                    }
                    case EMP : { /** Employee status (active/inactive) */
                        emp.setActive(valueMap.get("CDEMPSTATUS").equals("A"));
                        break;
                    }
                    case LEG : { /** Home address */
                        Address address = new Address();
                        address.setAddr1(valueMap.get("ADSTREET1"));
                        address.setAddr1(valueMap.get("ADSTREET2"));
                        address.setAddr1(valueMap.get("ADCITY"));
                        address.setAddr1(valueMap.get("ADSTATE"));
                        address.setAddr1(valueMap.get("ADZIPCODE"));
                        emp.setHomeAddress(address);
                        break;
                    }
                    case LOC : { /** Work location */
                        break;
                    }
                    case MAR : { /** Marital status */
                        emp.setMaritalStatus(MaritalStatus.valueOfCode(valueMap.get("CDMARITAL")));
                        break;
                    }
                    case NAM : { /** Employee's name */
                        emp.setFirstName(valueMap.get("NAFIRST"));
                        emp.setLastName(valueMap.get("NALAST"));
                        emp.setInitial(valueMap.get("NAMIDINIT"));
                        emp.setFullName(String.format("%s %s %s", emp.getFirstName(), emp.getInitial(), emp.getLastName()));
                        break;
                    }
                    case NAT : { /** Employee's title (Mr, Ms, etc.) */
                        emp.setTitle(valueMap.get("NATITLE"));
                        break;
                    }
                    case PHO : { /** Employee's phone number */
                        emp.setHomePhone(valueMap.get("ADPHONENUM"));
                        emp.setWorkPhone(valueMap.get("ADPHONENUMW"));
                        break;
                    }
                    case SEX : { /** Gender info */
                        emp.setGender(Gender.valueOf(valueMap.get("CDSEX")));
                        break;
                    }
                    case SUP : { /** T&A Supervisor id */
                        emp.setSupervisorId(Integer.parseInt(valueMap.get("NUXREFSV")));
                        break;
                    }
                    case TLE : { /** Job title */
                        break;
                    }
                    case TYP : { /** Pay type */
                        emp.setPayType(PayType.valueOf(valueMap.get("CDPAYTYPE")));
                        break;
                    }
                }
            }
        }
        return emp;
    }
}

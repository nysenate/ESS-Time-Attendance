package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static gov.nysenate.seta.model.TransactionType.*;

@Repository
public class SqlSupervisorDao extends SqlBaseDao implements SupervisorDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDao.class);

    @Autowired
    private EmployeeTransactionDao transHistoryDao;

    @Autowired
    private EmployeeDao employeeDao;

    protected static final String IS_EMP_ID_CURR_SUPERVISOR_SQL =
        "SELECT 1 FROM PM21PERSONN WHERE NUXREFSV = :empId";

    protected static final String GET_SUP_EMP_GROUP_SQL =
        "SELECT empList.*, per.NALAST, per.NUXREFSV, per.CDEMPSTATUS, " +
        "       ptx.CDTRANS, ptx.CDTRANSTYP, ptx.DTEFFECT, per.DTTXNORIGIN,\n" +
        "       MAX(DTEFFECT) OVER (PARTITION BY NUXREFEM, CDTRANS) AS LATEST_DTEFFECT,\n" +
        "       MAX(DTTXNORIGIN) OVER (PARTITION BY NUXREFEM, CDTRANS) AS LATEST_DTTXNORIGIN\n" +
        "FROM (\n" +
        "    SELECT DISTINCT 'PRIMARY' AS EMP_GROUP, NUXREFEM, NULL AS OVR_NUXREFSV\n" +
        "    FROM PM21PERAUDIT WHERE NUXREFSV = :supId \n" +
        "    UNION ALL\n" +
        "    SELECT DISTINCT\n" +
        "    CASE \n" +
        "        WHEN ovr.NUXREFSVSUB IS NOT NULL THEN 'SUP_OVR' \n" +
        "        WHEN ovr.NUXREFEMSUB IS NOT NULL THEN 'EMP_OVR' " +
        "    END,\n" +
        "    per.NUXREFEM, ovr.NUXREFSVSUB\n" +
        "    FROM PM23SUPOVRRD ovr\n" +
        "    LEFT JOIN PM21PERAUDIT per ON \n" +
        "      CASE WHEN ovr.NUXREFSVSUB IS NOT NULL AND per.NUXREFSV = ovr.NUXREFSVSUB THEN 1\n" +
        "           WHEN ovr.NUXREFEMSUB IS NOT NULL AND per.NUXREFEM = ovr.NUXREFEMSUB THEN 1\n" +
        "           ELSE 0\n" +
        "      END = 1\n" +
        "    WHERE ovr.NUXREFEM = :supId AND ovr.CDSTATUS = 'A'\n" +
        "    AND :endDate BETWEEN NVL(ovr.DTSTART, :endDate) AND NVL(ovr.DTEND, :endDate)\n" +
        "    AND per.NUXREFEM IS NOT NULL\n" +
        "  ) empList\n" +
        "JOIN PM21PERAUDIT per ON empList.NUXREFEM = per.NUXREFEM\n" +
        "JOIN PD21PTXNCODE ptx ON per.NUXREFEM = ptx.NUXREFEM AND per.NUCHANGE = ptx.NUCHANGE\n" +
        "WHERE \n" +
        "    (per.NUXREFEM NOT IN (SELECT DISTINCT NUXREFEM FROM PD21PTXNCODE WHERE CDTRANS IN ('APP', 'RTP', 'SUP'))\n" +
        "     OR ptx.CDTRANS IN ('APP', 'RTP', 'SUP', 'EMP'))\n" +
        "AND ptx.CDTRANSTYP = 'PER'\n" +
        "AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT <= :endDate\n" +
        "ORDER BY EMP_GROUP, NUXREFEM, DTEFFECT DESC";

    protected static final String GET_CURR_SUPERVISOR_FOR_EMP_SQL =
        "SELECT NUXREFSV FROM PM21PERSONN WHERE NUXREFEM = :empId";

    /**{@inheritDoc} */
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

    /**{@inheritDoc} */
    @Override
    public boolean isSupervisor(int empId, Date date) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("endDate", date);
        try {
            //remoteNamedJdbc.queryForObject(IS_EMP_ID_SUPERVISOR_DURING_DATE_SQL, params, Integer.class);
            return true;
        }
        catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    /**{@inheritDoc} */
    @Override
    public int getSupervisorIdForEmp(int empId) throws SupervisorException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        try {
            return remoteNamedJdbc.queryForObject(GET_CURR_SUPERVISOR_FOR_EMP_SQL, params, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("NUXREFSV");
                }
            });
        }
        catch (IncorrectResultSizeDataAccessException ex) {
            logger.warn("Failed to retrieve curr supervisor id for emp id: {} error: {}", empId, ex);
            throw new SupervisorNotFoundEx();
        }
    }

    /**{@inheritDoc} */
    @Override
    public int getSupervisorIdForEmp(int empId, Date date) throws SupervisorException {
        Set<TransactionType> transTypes = new HashSet<>(Arrays.asList(APP, RTP, SUP));
        Map<TransactionType, TransactionRecord> transMap =
                transHistoryDao.getLastTransactionRecords(empId, transTypes, date);

        /** The RTP/APP should have a supervisor id to use as the base */
        int supId = -1;
        TransactionRecord originalTrans = null;
        if (transMap.containsKey(RTP)) {
            originalTrans = transMap.get(RTP);
        }
        else if (transMap.containsKey(APP)) {
            originalTrans = transMap.get(APP);
        }

        if (originalTrans != null) {
            String supIdStr = originalTrans.getValueMap().get("NUXREFSV");
            if (StringUtils.isNumeric(supIdStr)) {
                supId = Integer.parseInt(supIdStr);
            }
        }

        /** The SUP transaction should occur after any RTP in order to take precedence */
        if (transMap.containsKey(SUP)) {
            TransactionRecord supTrans = transMap.get(SUP);
            if (originalTrans == null || supTrans.getEffectDate().compareTo(originalTrans.getEffectDate()) >= 0) {
                String supIdStr = supTrans.getValueMap().get("NUXREFSV");
                if (StringUtils.isNumeric(supIdStr)) {
                    supId = Integer.parseInt(supIdStr);
                }
            }
        }

        if (supId != -1) {
            return supId;
        }
        throw new SupervisorNotFoundEx("Supervisor id not found for empId: " + empId + " for date: " + date);
    }

    /**{@inheritDoc} */
    @Override
    public Supervisor getSupervisor(int supId) throws SupervisorException {
        Supervisor sup;
        try {
            Employee emp = employeeDao.getEmployeeById(supId);
            sup = new Supervisor(emp);
        }
        catch (EmployeeNotFoundEx ex) {
            throw new SupervisorNotFoundEx("Supervisor with id: " + supId + " not found.");
        }
        catch (EmployeeException ex) {
            throw new SupervisorException("Encountered error while retrieving supervisor with id: " + supId, ex);
        }
        return sup;
    }

    /**{@inheritDoc} */
    @Override
    public SupervisorChain getSupervisorChain(int supId) throws SupervisorException {
        return null;
    }

    /**{@inheritDoc} */
    @Override
    public SupervisorChain getSupervisorChain(int supId, Date date) throws SupervisorException {
        return null;
    }

    /**{@inheritDoc} */
    @Override
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Date start, Date end) throws SupervisorException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("supId", supId);
        params.addValue("endDate", end);
        List<Map<String, Object>> res = remoteNamedJdbc.query(GET_SUP_EMP_GROUP_SQL, params, new ColumnMapRowMapper());
        Iterator<Map<String,Object>> resIter = res.iterator();
        while (resIter.hasNext()) {
            logger.debug(resIter.next().toString());
        }
        return null;
    }
}

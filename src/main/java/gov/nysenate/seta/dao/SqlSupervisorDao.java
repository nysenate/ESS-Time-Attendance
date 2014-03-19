package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;
import gov.nysenate.seta.util.OutputUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

    /**
     * This query returns a listing of all supervisor related transactions for employees that have at
     * one point been assigned the given 'supId'. The results of this query can be processed to determine
     * valid employee groups for a supervisor.
     */
    protected static final String GET_SUP_EMP_TRANS_SQL =
        "SELECT empList.*, per.NALAST, per.NUXREFSV, per.CDEMPSTATUS, " +
        "       ptx.CDTRANS, ptx.CDTRANSTYP, ptx.DTEFFECT, per.DTTXNORIGIN,\n" +
        "       ROW_NUMBER() OVER (PARTITION BY EMP_GROUP, NUXREFEM ORDER BY DTEFFECT DESC, DTTXNORIGIN DESC) AS TRANS_RANK,\n" +
        "       FIRST_VALUE(DTEFFECT) OVER (PARTITION BY NUXREFEM, CDTRANS ORDER BY DTEFFECT DESC) AS LATEST_DTEFFECT,\n" +
        "       FIRST_VALUE(DTTXNORIGIN) OVER (PARTITION BY NUXREFEM, CDTRANS ORDER BY DTTXNORIGIN DESC) AS LATEST_DTTXNORIGIN\n" +
        "FROM (\n" +

        /**  Fetch the ids of the supervisor's direct employees. */
        "    SELECT DISTINCT 'PRIMARY' AS EMP_GROUP, NUXREFEM, NULL AS OVR_NUXREFSV\n" +
        "    FROM PM21PERAUDIT WHERE NUXREFSV = :supId \n" +

        /**  Combine that with the ids of the employees that are accessible through the sup overrides.
         *   The EMP_GROUP column will either be 'SUP_OVR' or 'EMP_OVR' to indicate the type of override. */
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

        /**  Retrieve just the APP/RTP/SUP/EMP transactions unless the employee doesn't
         *   have any of them (some earlier employees may be missing APP for example). */
        "WHERE \n" +
        "    (per.NUXREFEM NOT IN (SELECT DISTINCT NUXREFEM FROM PD21PTXNCODE WHERE CDTRANS IN ('APP', 'RTP', 'SUP'))\n" +
        "     OR ptx.CDTRANS IN ('APP', 'RTP', 'SUP', 'EMP'))\n" +
        "AND ptx.CDTRANSTYP = 'PER'\n" +
        "AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT <= :endDate\n" +
        "ORDER BY NUXREFEM, TRANS_RANK";

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
        List<Map<String, Object>> res = remoteNamedJdbc.query(GET_SUP_EMP_TRANS_SQL, params, new ColumnMapRowMapper());

        if (!res.isEmpty()) {
            SupervisorEmpGroup empGroup = new SupervisorEmpGroup();
            Set<TransactionType> supTransTypes = new HashSet<>(Arrays.asList(SUP,APP,RTP));
            Map<Integer, EmployeeSupInfo> primaryEmps = new HashMap<>();
            Map<Integer, EmployeeSupInfo> overrideEmps = new HashMap<>();
            Map<Integer, Map<Integer, EmployeeSupInfo>> supOverrideEmps = new HashMap<>();

            Map<String, Map<Integer, Date>> possibleEmps = new HashMap<>();
            possibleEmps.put("PRIMARY", new HashMap<Integer, Date>());
            possibleEmps.put("EMP_OVR", new HashMap<Integer, Date>());
            possibleEmps.put("SUP_OVR", new HashMap<Integer, Date>());

            for (Map<String,Object> colMap : res) {
                logger.info(colMap.toString());
                /** Extract the column data */
                String group = colMap.get("EMP_GROUP").toString();
                int empId = Integer.parseInt(colMap.get("NUXREFEM").toString());
                TransactionType transType = TransactionType.valueOf(colMap.get("CDTRANS").toString());
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");
                Date effectDate = formatter.parseDateTime(colMap.get("DTEFFECT").toString()).toDate();
                Date originDate = formatter.parseDateTime(colMap.get("DTTXNORIGIN").toString()).toDate();
                int rank = Integer.parseInt(colMap.get("TRANS_RANK").toString());

                if (colMap.get("NUXREFSV") == null || !StringUtils.isNumeric(colMap.get("NUXREFSV").toString())) {
                    continue;
                }

                int currSupId = Integer.parseInt(colMap.get("NUXREFSV").toString());
                EmployeeSupInfo empSupInfo = new EmployeeSupInfo(empId, start, end);
                empSupInfo.setSupId(currSupId);
                empSupInfo.setEmpLastName(colMap.get("NALAST").toString());
                if (supTransTypes.contains(transType)) {
                    empSupInfo.setSupStartDate(effectDate);
                }

                /** The first record will contain the most recent personnel transaction */
                if (rank == 1) {
                    /** If the last transaction happened before the given start date, we don't have
                     *  to worry about a possible supervisor change during the given range. */
                    if (effectDate.before(start)) {
                        /** Skip the employee if they left before the given start date */
                        if (transType.equals(EMP)) {
                            continue;
                        }
                        switch (group) {
                            case "PRIMARY": {
                                if (currSupId == supId) {
                                    primaryEmps.put(empId, empSupInfo);
                                }
                                else {
                                    continue;
                                }
                                break;
                            }
                            case "EMP_OVR": {
                                overrideEmps.put(empId, empSupInfo);
                                break;
                            }
                            case "SUP_OVR": {
                                int ovrSupId = Integer.parseInt(colMap.get("OVR_NUXREFSV").toString());
                                if (currSupId == ovrSupId) {
                                    if (!supOverrideEmps.containsKey(ovrSupId)) {
                                        supOverrideEmps.put(ovrSupId, new HashMap<Integer, EmployeeSupInfo>());
                                    }
                                    supOverrideEmps.get(ovrSupId).put(empId, empSupInfo);
                                }
                                else {
                                    continue;
                                }
                                break;
                            }
                        }
                    }
                    /** We might have to worry about a possible supervisor change during the date range */
                    else {
                        if (transType.equals(EMP)) {
                            possibleEmps.get(group).put(empId, effectDate);
                            continue;
                        }
                        switch (group) {
                            case "PRIMARY": {
                                if (currSupId == supId) {
                                    primaryEmps.put(empId, empSupInfo);
                                }
                                else {
                                    possibleEmps.get("PRIMARY").put(empId, effectDate);
                                }
                                break;
                            }
                            case "EMP_OVR": {
                                overrideEmps.put(empId, empSupInfo);
                                break;
                            }
                            case "SUP_OVR": {
                                int ovrSupId = Integer.parseInt(colMap.get("OVR_NUXREFSV").toString());
                                if (currSupId == ovrSupId) {
                                    if (!supOverrideEmps.containsKey(ovrSupId)) {
                                        supOverrideEmps.put(ovrSupId, new HashMap<Integer, EmployeeSupInfo>());
                                    }
                                    supOverrideEmps.get(ovrSupId).put(empId, empSupInfo);
                                }
                                else {
                                    possibleEmps.get("SUP_OVR").put(empId, effectDate);
                                }
                                break;
                            }
                        }
                    }
                }
                else {
                    if (possibleEmps.get(group).containsKey(empId)) {
                        switch (group) {
                            case "PRIMARY": {
                                if (currSupId == supId) {
                                    empSupInfo.setSupEndDate(possibleEmps.get(group).get(empId));
                                    primaryEmps.put(empId, empSupInfo);
                                }
                                else {
                                    if (effectDate.before(start)) {
                                        possibleEmps.get(group).remove(empId);
                                    }
                                }
                                break;
                            }
                            case "SUP_OVR": {
                                int ovrSupId = Integer.parseInt(colMap.get("OVR_NUXREFSV").toString());
                                if (currSupId == ovrSupId) {
                                    empSupInfo.setSupEndDate(possibleEmps.get(group).get(empId));
                                    supOverrideEmps.get(ovrSupId).put(empId, empSupInfo);
                                }
                                else {
                                    if (effectDate.before(start)) {
                                        possibleEmps.get(group).remove(empId);
                                    }
                                }
                                break;
                            }
                        }
                        logger.info("Possible emp: " + empId + " rank " + rank);
                    }
                }
            }
            logger.info(OutputUtils.toJson(primaryEmps));
            logger.info(OutputUtils.toJson(overrideEmps));
            logger.info(OutputUtils.toJson(supOverrideEmps));
        }


        return null;
    }
}

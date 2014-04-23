package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.exception.*;
import gov.nysenate.seta.model.personnel.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

import static gov.nysenate.seta.model.personnel.TransactionType.*;

@Repository
public class SqlSupervisorDao extends SqlBaseDao implements SupervisorDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDao.class);

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private EmployeeTransactionDao empTransactionDao;

    /**
     * This query returns a listing of all supervisor related transactions for employees that have at
     * one point been assigned the given 'supId'. The results of this query can be processed to determine
     * valid employee groups for a supervisor.
     */
    protected static final String GET_SUP_EMP_TRANS_SQL =
        "SELECT empList.*, per.NALAST, per.NUXREFSV, per.CDEMPSTATUS, " +
        "       ptx.CDTRANS, ptx.CDTRANSTYP, ptx.DTEFFECT, per.DTTXNORIGIN,\n" +
        "       ROW_NUMBER() " +
        "       OVER (PARTITION BY EMP_GROUP, NUXREFEM, OVR_NUXREFSV ORDER BY DTEFFECT DESC, DTTXNORIGIN DESC) AS TRANS_RANK\n" +
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

    protected static final String GET_SUP_CHAIN_EXCEPTIONS =
        "SELECT NUXREFEM, NUXREFSV, CDTYPE, CDSTATUS FROM PM23SPCHNEX WHERE CDSTATUS = 'A' AND NUXREFEM = :empId";

    /**{@inheritDoc}
     *
     * We determine this by simply checking if the empId managed any employees during the
     * given time range.
     */
    @Override
    public boolean isSupervisor(int empId, Date start, Date end) {
        SupervisorEmpGroup supervisorEmpGroup;
        try {
            supervisorEmpGroup = getSupervisorEmpGroup(empId, start, end);
        }
        catch (SupervisorException ex) {
            return false;
        }
        return supervisorEmpGroup.hasEmployees();
    }

    /**{@inheritDoc}
     *
     * The latest employee transactions before the given 'date' are checked to determine
     * the supervisor id.
     */
    @Override
    public int getSupervisorIdForEmp(int empId, Date date) throws SupervisorException {
        Set<TransactionType> transTypes = new HashSet<>(Arrays.asList(APP, RTP, SUP));
        TransactionHistory transHistory = empTransactionDao.getTransHistory(empId, transTypes, date);

        int supId = -1;
        if (transHistory.hasRecords()) {
            TransactionRecord latestSupRec = transHistory.getAllTransRecords(false).getFirst();
            String supIdStr = latestSupRec.getValueMap().get("NUXREFSV");
            if (StringUtils.isNumeric(supIdStr)) {
                supId = Integer.parseInt(supIdStr);
            }
        }

        if (supId != -1) {
            return supId;
        }
        throw new SupervisorNotFoundEx("Supervisor id not found for empId: " + empId + " for date: " + date);
    }

    /**
     * Determine the chain by recursively retrieving the supervisor id for each successive employee.
     * {@inheritDoc}
     */
    @Override
    public SupervisorChain getSupervisorChain(int empId, Date date) throws SupervisorException {
        int currEmpId = empId;
        int currDepth = 0;
        final int maxDepth = 10;
        SupervisorChain chain = new SupervisorChain(currEmpId);

        while (true) {
            int currSupId = getSupervisorIdForEmp(currEmpId, date);
            if (!chain.containsSupervisor(currSupId)) {
                chain.addSupervisorToChain(currSupId);
                currEmpId = currSupId;
            }
            else {
                break;
            }
            /** Eliminate possibility of infinite recursion. */
            if (currDepth >= maxDepth) {
                break;
            }
            currDepth++;
        }

        /** Look for any active inclusions/exclusions */
        SqlParameterSource params = new MapSqlParameterSource("empId", empId);
        List<Map<String, Object>> res = remoteNamedJdbc.query(GET_SUP_CHAIN_EXCEPTIONS, params, new ColumnMapRowMapper());
        if (!res.isEmpty()) {
            for (Map<String, Object> row : res) {
                int supId = Integer.parseInt(row.get("NUXREFSV").toString());
                if (row.get("CDTYPE").equals("I")) {
                    chain.getChainInclusions().add(supId);
                }
                else {
                    chain.getChainExclusions().add(supId);
                }
            }
        }

        return chain;
    }

    /**{@inheritDoc} */
    @Override
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Date start, Date end) throws SupervisorException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("supId", supId);
        params.addValue("endDate", end);
        List<Map<String, Object>> res;
        try {
            res = remoteNamedJdbc.query(GET_SUP_EMP_TRANS_SQL, params, new ColumnMapRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            throw new SupervisorException("Failed to retrieve matching employees for supId: " + supId + " before: " + end);
        }

        /**
         * The transactions for the matching employees need to be processed to determine if they
         * are still under the given supervisor.
         */
        if (!res.isEmpty()) {
            SupervisorEmpGroup empGroup = new SupervisorEmpGroup(supId, start, end);
            Set<TransactionType> supTransTypes = new HashSet<>(Arrays.asList(SUP,APP,RTP));
            Map<Integer, EmployeeSupInfo> primaryEmps = new HashMap<>();
            Map<Integer, EmployeeSupInfo> overrideEmps = new HashMap<>();
            Map<Integer, Map<Integer, EmployeeSupInfo>> supOverrideEmps = new HashMap<>();

            Map<Integer, Date> possiblePrimaryEmps = new HashMap<>();
            Map<Integer, Map<Integer, Date>> possibleSupOvrEmps = new HashMap<>();

            for (Map<String,Object> colMap : res) {
                logger.trace(colMap.toString());
                String group = colMap.get("EMP_GROUP").toString();
                int empId = Integer.parseInt(colMap.get("NUXREFEM").toString());
                TransactionType transType = TransactionType.valueOf(colMap.get("CDTRANS").toString());
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");
                Date effectDate = formatter.parseDateTime(colMap.get("DTEFFECT").toString()).toDate();
                int rank = Integer.parseInt(colMap.get("TRANS_RANK").toString());
                boolean effectDateIsPast = effectDate.compareTo(start) <= 0;

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

                boolean empTerminated = transType.equals(EMP);
                if (empTerminated) {
                    empSupInfo.setSupEndDate(effectDate);
                }

                /**
                 * The first rank record for a given empId contains latest transaction that took effect
                 * before/on the given 'end' date.
                 */
                if (rank == 1) {
                    /**
                     * Add the employee to their supervisor's respective group if their supervisor id
                     * matches the given 'supId'. For PRIMARY AND SUP_OVR types we flag mismatches as possible
                     * employees when the effect date is between the 'start' and 'end' dates. The proceeding
                     * record(s) for those employees will then need to be checked to see if the supervisor matches
                     * at some point on/after the 'start' date.
                     */
                    switch (group) {
                        case "PRIMARY": {
                            if (currSupId == supId && !empTerminated) {
                                primaryEmps.put(empId, empSupInfo);
                            }
                            else if (!effectDateIsPast) {
                                possiblePrimaryEmps.put(empId, effectDate);
                            }
                            break;
                        }
                        case "EMP_OVR": {
                            if (empTerminated && effectDateIsPast) {
                                continue;
                            }
                            overrideEmps.put(empId, empSupInfo);
                            break;
                        }
                        case "SUP_OVR": {
                            int ovrSupId = Integer.parseInt(colMap.get("OVR_NUXREFSV").toString());
                            if (currSupId == ovrSupId && !empTerminated) {
                                if (!supOverrideEmps.containsKey(ovrSupId)) {
                                    supOverrideEmps.put(ovrSupId, new HashMap<Integer, EmployeeSupInfo>());
                                }
                                supOverrideEmps.get(ovrSupId).put(empId, empSupInfo);
                            }
                            else if (!effectDateIsPast) {
                                if (!possibleSupOvrEmps.containsKey(ovrSupId)) {
                                    possibleSupOvrEmps.put(ovrSupId, new HashMap<Integer, Date>());
                                }
                                possibleSupOvrEmps.get(ovrSupId).put(empId, effectDate);
                            }
                            break;
                        }
                    }
                }
                else {
                    /**
                     * Process the records of employees that had a supervisor change during the date range.
                     * If a supervisor match is found to occur on/before the 'start' date, we add them to their
                     * respective supervisor group. Otherwise if we can't find a match and the effect date has
                     * occurred before the 'start' date, we know that they don't belong in the group for this range.
                     */
                    switch (group) {
                        case "PRIMARY": {
                            if (possiblePrimaryEmps.containsKey(empId)) {
                                if (currSupId == supId && !empTerminated) {
                                    empSupInfo.setSupEndDate(possiblePrimaryEmps.get(empId));
                                    primaryEmps.put(empId, empSupInfo);
                                }
                                else if (!effectDateIsPast) {
                                    possiblePrimaryEmps.put(empId, effectDate);
                                }
                                else {
                                    possiblePrimaryEmps.remove(empId);
                                }
                            }
                            break;
                        }
                        case "SUP_OVR": {
                            int ovrSupId = Integer.parseInt(colMap.get("OVR_NUXREFSV").toString());
                            if (possibleSupOvrEmps.containsKey(ovrSupId) && possibleSupOvrEmps.get(ovrSupId).containsKey(empId)) {
                                if (currSupId == ovrSupId && !empTerminated) {
                                    empSupInfo.setSupEndDate(possibleSupOvrEmps.get(ovrSupId).get(empId));
                                    supOverrideEmps.get(ovrSupId).put(empId, empSupInfo);
                                }
                                else if (!effectDateIsPast) {
                                    possibleSupOvrEmps.get(ovrSupId).put(empId, effectDate);
                                }
                                else {
                                    possibleSupOvrEmps.get(ovrSupId).remove(empId);
                                }
                            }
                            break;
                        }
                    }
                }
            }

            empGroup.setPrimaryEmployees(primaryEmps);
            empGroup.setOverrideEmployees(overrideEmps);
            empGroup.setSupOverrideEmployees(supOverrideEmps);
            return empGroup;
        }
        throw new SupervisorMissingEmpsEx("No employee associations could be found for supId: " + supId + " before " + end);
    }

    @Override
    public void setSupervisorOverride(int supId, int ovrSupId, Date start, Date end) throws SupervisorException {
        throw new NotImplementedException();
    }
}

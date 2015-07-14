package gov.nysenate.seta.dao.personnel;

import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.transaction.SqlEmpTransactionDao;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.exception.SupervisorMissingEmpsEx;
import gov.nysenate.seta.model.exception.SupervisorNotFoundEx;
import gov.nysenate.seta.model.personnel.EmployeeSupInfo;
import gov.nysenate.seta.model.personnel.SupervisorChain;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gov.nysenate.common.DateUtils.endOfDateRange;
import static gov.nysenate.common.DateUtils.startOfDateRange;
import static gov.nysenate.seta.dao.personnel.SqlSupervisorQuery.GET_SUP_CHAIN_EXCEPTIONS;
import static gov.nysenate.seta.dao.personnel.SqlSupervisorQuery.GET_SUP_EMP_TRANS_SQL;
import static gov.nysenate.seta.model.transaction.TransactionCode.*;

@Repository
public class SqlSupervisorDao extends SqlBaseDao implements SupervisorDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDao.class);

    @Autowired
    private SqlEmpTransactionDao empTransDao;

    /**
     * {@inheritDoc}
     * We determine this by simply checking if the empId managed any employees during the
     * given time range.
     */
    @Override
    public boolean isSupervisor(int empId, Range<LocalDate> dateRange) {
        SupervisorEmpGroup supervisorEmpGroup;
        try {
            supervisorEmpGroup = getSupervisorEmpGroup(empId, dateRange);
        }
        catch (SupervisorException ex) {
            return false;
        }
        return supervisorEmpGroup.hasEmployees();
    }

    /**
     * {@inheritDoc}
     * The latest employee transactions before the given 'date' are checked to determine
     * the supervisor id.
     */
    @Override
    public int getSupervisorIdForEmp(int empId, LocalDate date) throws SupervisorException {
        Set<TransactionCode> transCodes = new HashSet<>(Arrays.asList(APP, RTP, SUP));
        TransactionHistory transHistory =
            empTransDao.getTransHistory(empId, transCodes, Range.atMost(date), EmpTransDaoOption.INITIALIZE_AS_APP);

        int supId = -1;
        if (transHistory.hasRecords()) {
            TransactionRecord latestSupRec = transHistory.getAllTransRecords(SortOrder.DESC).getFirst();
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
    public SupervisorChain getSupervisorChain(int empId, LocalDate date) throws SupervisorException {
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
        List<Map<String, Object>> res = remoteNamedJdbc.query(GET_SUP_CHAIN_EXCEPTIONS.getSql(), params, new ColumnMapRowMapper());
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
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        LocalDate startDate = startOfDateRange(dateRange);
        LocalDate endDate = endOfDateRange(dateRange);
        params.addValue("supId", supId);
        params.addValue("endDate", toDate(endDate));
        List<Map<String, Object>> res;
        try {
            res = remoteNamedJdbc.query(GET_SUP_EMP_TRANS_SQL.getSql(), params, new ColumnMapRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            throw new SupervisorException("Failed to retrieve matching employees for supId: " + supId + " before: " + endDate);
        }

        /**
         * The transactions for the matching employees need to be processed to determine if they
         * are still under the given supervisor.
         */
        if (!res.isEmpty()) {
            SupervisorEmpGroup empGroup = new SupervisorEmpGroup(supId, startDate, endDate);
            Set<TransactionCode> supTransCodes = new HashSet<>(Arrays.asList(SUP,APP,RTP));
            Map<Integer, EmployeeSupInfo> primaryEmps = new HashMap<>();
            Map<Integer, EmployeeSupInfo> overrideEmps = new HashMap<>();
            Map<Integer, Map<Integer, EmployeeSupInfo>> supOverrideEmps = new HashMap<>();

            Map<Integer, LocalDate> possiblePrimaryEmps = new HashMap<>();
            Map<Integer, Map<Integer, LocalDate>> possibleSupOvrEmps = new HashMap<>();

            for (Map<String,Object> colMap : res) {
                logger.trace(colMap.toString());
                String group = colMap.get("EMP_GROUP").toString();
                int empId = Integer.parseInt(colMap.get("NUXREFEM").toString());
                TransactionCode transType = TransactionCode.valueOf(colMap.get("CDTRANS").toString());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDate effectDate = LocalDate.from(formatter.parse(colMap.get("DTEFFECT").toString()));
                int rank = Integer.parseInt(colMap.get("TRANS_RANK").toString());
                boolean effectDateIsPast = effectDate.compareTo(startDate) <= 0;

                if (colMap.get("NUXREFSV") == null || !StringUtils.isNumeric(colMap.get("NUXREFSV").toString())) {
                    continue;
                }

                int currSupId = Integer.parseInt(colMap.get("NUXREFSV").toString());
                EmployeeSupInfo empSupInfo = new EmployeeSupInfo(empId, startDate, endDate);
                empSupInfo.setSupId(currSupId);
                empSupInfo.setEmpLastName(colMap.get("NALAST").toString());
                if (supTransCodes.contains(transType)) {
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
                     * matches the given 'supId'. For PRIMARY AND SUP_OVR codes we flag mismatches as possible
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
                                    supOverrideEmps.put(ovrSupId, new HashMap<>());
                                }
                                supOverrideEmps.get(ovrSupId).put(empId, empSupInfo);
                            }
                            else if (!effectDateIsPast) {
                                if (!possibleSupOvrEmps.containsKey(ovrSupId)) {
                                    possibleSupOvrEmps.put(ovrSupId, new HashMap<>());
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
        throw new SupervisorMissingEmpsEx("No employee associations could be found for supId: " + supId + " before " + endDate);
    }

    @Override
    public void setSupervisorOverride(int supId, int ovrSupId, Range<LocalDate> dateRange) throws SupervisorException {
        throw new NotImplementedException();
    }
}

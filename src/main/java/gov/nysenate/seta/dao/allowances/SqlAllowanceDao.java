package gov.nysenate.seta.dao.allowances;


import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.dao.allowances.mapper.AllowanceRowMapper;
import gov.nysenate.seta.dao.allowances.mapper.AmountExceedRowMapper;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.allowances.AllowanceUsage;
import gov.nysenate.seta.model.payroll.SalaryRec;
import gov.nysenate.seta.model.transaction.AuditHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class SqlAllowanceDao extends SqlBaseDao implements AllowanceDao
{
    private static final Logger logger = LoggerFactory.getLogger(AllowanceUsage.class);

    /** --- SQL Queries --- */

    protected static final String GET_ALLOWANCE_USAGE_SQL =
        "WITH TE_PAY AS \n" +
         "            (SELECT /*+ MATERIALIZE */  A.NUXREFEM, A.NUDOCUMENT, A.DTEFFECT, A.DTENDTE, A.NUHRHRSPD, A.MOTOTHRSPD, A.MOPRIORYRTE, C.MOAMTEXCEED, A.MOSALBIWKLY, A.DTTXNORIGIN, B.DTEND, C.DTAPPOINTFRM, C.DTAPPOINT, C.DTCONTSERV, A.ROWID ROWIDCUR,  LAST_VALUE(A.DTENDTE) OVER (PARTITION BY A.NUXREFEM,A.NUDOCUMENT ORDER BY  A.DTENDTE, A.DTTXNORIGIN ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) DTENDTELAST, LAST_VALUE(A.ROWID) OVER (PARTITION BY A.NUXREFEM,A.NUDOCUMENT ORDER BY  A.DTENDTE, A.DTTXNORIGIN  ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)  ROWIDMAX, A.NULINE, LAST_VALUE(A.NULINE) OVER (PARTITION BY A.NUXREFEM ORDER BY  A.DTTXNORIGIN  ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) NULINELAST\n "+
         "    FROM SASS_OWNER.PM21PERAUDIT A\n "+
         "    JOIN SASS_OWNER.SL16PERIOD B ON (     A.DTEFFECT BETWEEN B.DTBEGIN AND B.DTEND\n"+
         "            AND B.CDPERIOD = 'PF'\n"+
         "            AND B.CDSTATUS = 'A')\n"+
         "    JOIN SASS_OWNER.PM21PERSONN C ON (C.NUXREFEM = A.NUXREFEM)\n"+
         "    WHERE A.NUXREFEM = :empId\n"+
         "    AND A.NUDOCUMENT LIKE 'T%'\n"+
         "    AND A.DTENDTE >= :janDate\n"+
         "    AND A.CDSTATUS = 'A'\n"+
         "            )\n"+
         "    SELECT MIN(NUXREFEM) NUXREFEM, SUM(NUHRHRSPD) AS TE_HRS_PAID, SUM(NVL(MOTOTHRSPD, 0)) - SUM(NVL(MOPRIORYRTE,0)) AS TE_AMOUNT_PAID, (SUM(NVL(MOTOTHRSPD, 0)) -  SUM(NVL(MOPRIORYRTE,0)) ) MOTESPEND,  MAX(DTENDTE) DTENDTE\n"+
         "    FROM TE_PAY\n"+
         "    WHERE ROWIDMAX = ROWIDCUR\n"+
         "    AND NULINE = NULINELAST";

    protected static final String GET_AMOUNT_EXCEED_POT_SQL =
        " SELECT b.moamtexceed, b.dteffect, b.dttxnorigin, a.nuxrefem " +
        "   FROM SASS_OWNER.pd21ptxncode a" +
        "   JOIN SASS_OWNER.PM21PERAUDIT b ON (a.nuxrefem = b.nuxrefem AND a.nuchange = b.nuchange) " +
        "  WHERE A.NUXREFEM = :empId " +
        "    AND a.dteffect <= :decDate " +
        "    AND a.cdtrans = 'EXC'" +
        "    AND a.cdstatus = 'A'" +
        "    AND b.cdstatus = 'A'" +
        " ORDER BY b.dteffect DESC, b.dttxnorigin DESC";

    protected static final String GET_SALARY_POT_SQL =
        " SELECT b.mosalbiwkly, b.dteffect, b.dttxnorigin, a.nuxrefem " +
        "   FROM SASS_OWNER.pd21ptxncode a" +
        "   JOIN SASS_OWNER.PM21PERAUDIT b ON (a.nuxrefem = b.nuxrefem AND a.nuchange = b.nuchange) " +
        "  WHERE A.NUXREFEM = :empId " +
        "    AND a.cdtrans = 'SAL'" +
        "    AND a.cdstatus = 'A'" +
        "    AND b.cdstatus = 'A'" +
        " ORDER BY b.dteffect DESC, b.dttxnorigin DESC";

//    protected static final String GET_ALLOWANCE_USAGE_SQL =

    /** {@inheritDoc} */
    @Override
     public AllowanceUsage getAllowanceUsage(int empId, int year, AuditHistory auditHistory) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        LocalDate janDate = LocalDate.of(year, 1, 1);
        params.addValue("empId", empId);
        params.addValue("janDate", toDate(janDate));
        LinkedList<AllowanceUsage> annualAllowanceRecs;
        annualAllowanceRecs = new LinkedList<>(remoteNamedJdbc.query(GET_ALLOWANCE_USAGE_SQL, params,
                new AllowanceRowMapper("")));

        params = new MapSqlParameterSource();
        LocalDate decDate = LocalDate.of(year, 12, 31);
        params.addValue("empId", empId);
        params.addValue("decDate", toDate(decDate));

        LinkedList<BigDecimal> amountExceedRecs = null;
        amountExceedRecs = new LinkedList<>(remoteNamedJdbc.query(GET_AMOUNT_EXCEED_POT_SQL, params,
                new AmountExceedRowMapper("")));

        if (amountExceedRecs==null || amountExceedRecs.size()==0) {
            annualAllowanceRecs.get(0).setMoneyAllowed(null);
        }
        else {
            annualAllowanceRecs.get(0).setMoneyAllowed(amountExceedRecs.get(0));
        }

        LinkedList<SalaryRec> salaries = null;
        //params = new MapSqlParameterSource();
        //params.addValue("empId", empId);
        //salaries = new LinkedList<>(remoteNamedJdbc.query(GET_SALARY_POT_SQL, params,
              //new SalaryRowMapper("")));
        Map<String, String> matchValues = new HashMap<String, String>();
        matchValues.put("CDPAYTYPE", "TE");
        String[] columnChangeFilter =  {"MOSALBIWKLY"};

        salaries = setSalaryRecs(auditHistory.getMatchedAuditRecords(matchValues, true, columnChangeFilter));

        annualAllowanceRecs.get(0).setSalaryRecs(salaries);

        return annualAllowanceRecs.get(0);
    }

    protected LinkedList<SalaryRec> setSalaryRecs(List<Map<String, String>> auditRecs) {
        LinkedList<SalaryRec> salaries = null;
        salaries = new LinkedList<SalaryRec>();
        Map<String, String> currentAuditRec = null;

        for (int x=0;x <auditRecs.size(); x++) {
            SalaryRec salaryRec = new SalaryRec();
            currentAuditRec = auditRecs.get(x);
            logger.debug(OutputUtils.toJson(currentAuditRec));

            salaryRec.setSalary(new BigDecimal(currentAuditRec.get("MOSALBIWKLY")));
            try {
                salaryRec.setEffectDate(new SimpleDateFormat("MM/dd/yyyy").parse(currentAuditRec.get("EffectDate")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            salaries.add(salaryRec);
        }

        return salaries;
    }

}



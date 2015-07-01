package gov.nysenate.seta.dao.accrual;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import gov.nysenate.seta.dao.accrual.mapper.AnnualAccSummaryRowMapper;
import gov.nysenate.seta.dao.accrual.mapper.PeriodAccSummaryRowMapper;
import gov.nysenate.seta.dao.accrual.mapper.PeriodAccUsageRowMapper;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.accrual.AnnualAccSummary;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.accrual.PeriodAccUsage;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.util.DateUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

import static gov.nysenate.seta.dao.accrual.SqlAccrualQuery.*;

/** {@inheritDoc}
 *
 *  The accrual data is stored primarily in three database tables in SFMS:
 *  PM23ATTEND - Stores annual rolling counts of accrued/used hours as well as the number of pay periods worked.
 *  PD23ACCUSAGE - Stores counts of accrued/used hours on a pay period basis as well as the expected YTD hours to work.
 *  PD23ATTEND - Stores usage hours on a pay period basis (updated more frequently).
 *
 *  PM23ATTEND and PD23ACCUSAGE are refreshed with a substantial delay from the end of the prior pay period such
 *  that it gives employees enough time to submit old timesheets without having to recompute data constantly. However
 *  the side effect is that there will likely not be accrual information for the more recent pay periods. The
 *  accruals will have to be computed to account for the missing data via the service layer.
 */
@Service("sqlAccrual")
public class NewSqlAccrualDao extends SqlBaseDao implements AccrualDao
{
    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public TreeMap<PayPeriod, PeriodAccSummary> getPeriodAccrualSummaries(int empId, int year, LocalDate beforeDate) {
        MapSqlParameterSource params = getPeriodAccSummaryParams(empId, year, beforeDate);
        List<PeriodAccSummary> periodAccSummaries =
            remoteNamedJdbc.query(GET_PERIOD_ACC_SUMMARIES.getSql(), params, new PeriodAccSummaryRowMapper("",""));
        return new TreeMap<>(Maps.uniqueIndex(periodAccSummaries, PeriodAccSummary::getBasePayPeriod));
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<Integer, AnnualAccSummary> getAnnualAccrualSummaries(int empId, int endYear) {
        MapSqlParameterSource params = getAnnualAccSummaryParams(empId, endYear);
        List<AnnualAccSummary> annualAccRecs =
            remoteNamedJdbc.query(GET_ANNUAL_ACC_SUMMARIES.getSql(), params, new AnnualAccSummaryRowMapper());
        return new TreeMap<>(Maps.uniqueIndex(annualAccRecs, AnnualAccSummary::getYear));
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<PayPeriod, PeriodAccUsage> getPeriodAccrualUsages(int empId, Range<LocalDate> dateRange) {
        MapSqlParameterSource params = getAccrualUsageParams(empId, DateUtils.startOfDateRange(dateRange),
                                                                    DateUtils.endOfDateRange(dateRange));
        List<PeriodAccUsage> usageRecs =
            remoteNamedJdbc.query(GET_PERIOD_ACCRUAL_USAGE.getSql(), params, new PeriodAccUsageRowMapper("",""));
        return new TreeMap<>(Maps.uniqueIndex(usageRecs, PeriodAccUsage::getPayPeriod));
    }

    /** --- Param Source Methods --- */

    protected MapSqlParameterSource getAnnualAccSummaryParams(int empId, int endYear) {
        return new MapSqlParameterSource()
            .addValue("empId", empId)
            .addValue("endYear", endYear);
    }

    protected MapSqlParameterSource getPeriodAccSummaryParams(int empId, int year, LocalDate endDate) {
        return new MapSqlParameterSource()
            .addValue("empId", empId)
            .addValue("prevYear", year - 1)
            .addValue("beforeDate", DateUtils.toDate(endDate));
    }

    protected MapSqlParameterSource getAccrualUsageParams(int empId, LocalDate start, LocalDate end) {
        return new MapSqlParameterSource()
            .addValue("empId", empId)
            .addValue("startDate", DateUtils.toDate(start))
            .addValue("endDate", DateUtils.toDate(end));
    }
}
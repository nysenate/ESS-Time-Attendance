package gov.nysenate.seta.dao.accrual;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.accrual.AnnualAccSummary;
import gov.nysenate.seta.model.accrual.AnnualAccrualUsage;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.accrual.PeriodAccUsage;
import gov.nysenate.seta.model.period.PayPeriod;

import java.time.LocalDate;
import java.util.TreeMap;

/**
 * Data access layer for retrieving and computing accrual information
 * (e.g personal hours, vacation hours, etc).
 */
public interface AccrualDao extends BaseDao
{
    /**
     * Retrieve the per-pay-period accrual summaries for the given employee that occur before a specific date.
     * A TreeMap is returned which maps the PeriodAccSummary object with it's associated 'basePayPeriod'.
     *
     * @param empId int - Employee id
     * @param year int - The year to retrieve accruals for
     * @param beforeDate LocalDate - The retrieved period summaries will be effective prior to this date.
     * @return TreeMap<LocalDate, PeriodAccSummary>
     */
    TreeMap<PayPeriod, PeriodAccSummary> getPeriodAccruals(int empId, int year, LocalDate beforeDate);

    /**
     * Retrieve the running annual accrual summaries for the given employee for all years before or on the 'endYear'.
     *
     * @param empId int - Employee id
     * @param endYear int - The year to retrieve annual summaries until.
     * @return TreeMap<Integer, AnnualAccSummary>
     */
    TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear);

    /**
     * Retrieve the period accrual usage objects that represent the hours charged during a given pay period.
     *
     * @param empId int - Employee id
     * @param dateRange Range<LocalDate> - The date range to obtain usages within
     * @return TreeMap<PayPeriod, PeriodAccUsage>
     */
    TreeMap<PayPeriod, PeriodAccUsage> getPeriodAccrualUsages(int empId, Range<LocalDate> dateRange);

}
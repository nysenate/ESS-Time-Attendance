package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.PeriodAccrualSummary;
import gov.nysenate.seta.model.period.PayPeriod;

import java.util.List;

/**
 * Data access layer for retrieving and computing accrual information
 * (e.g personal hours, vacation hours, etc).
 */
public interface AccrualDao extends BaseDao
{
    /**
     * Retrieve accruals information for the given employee id and a specific pay period.
     * @param empId int - Employee id
     * @param payPeriod Date - Pay period to get accrual for
     * @param earliestRecLikeAppoint boolean - If true, the earliest effect date record will be treated like an appoint record
     *                               (ie: it will initialize all columns from the first record, regardless if it is an appoint,
     *                               reappoint, or some other transaction)
     * @return PeriodAccrualSummary if found, throws AccrualException otherwise.
     * @throws  AccrualException
     */
    public PeriodAccrualSummary getAccuralSummary(int empId, PayPeriod payPeriod, boolean earliestRecLikeAppoint) throws AccrualException;

    /**
     * Retrieve a history of accruals for the given employee and the list of pay periods.
     * @param empId int - Employee id
     * @param payPeriods List<PayPeriod> - Attendance pay period dates to build history from
     * @return PeriodAccrualSummary
     * @throws AccrualException
     */
    public List<PeriodAccrualSummary> getAccrualSummaries(int empId, List<PayPeriod> payPeriods) throws AccrualException;
}

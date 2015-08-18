package gov.nysenate.seta.service.accrual;

import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.period.PayPeriod;

import java.util.List;
import java.util.TreeMap;

/**
 * Service interface to provide accrual related functionality.
 */
public interface AccrualComputeService
{
    /**
     * Computes the accruals available for an employee at the start of the given pay period.
     *
     * @param empId int - Employee id to get accruals for.
     * @param payPeriod PayPeriod - Accruals will be valid at the start of this pay period.
     * @return PeriodAccSummary
     * @throws AccrualException - If there is an exception during either retrieval or computation of the accruals.
     */
    public PeriodAccSummary getAccruals(int empId, PayPeriod payPeriod) throws AccrualException;

    /**
     * Retrieves a collection of accrual summaries for each of the pay periods within the given 'payPeriods'.
     *
     * @param empId int - Employee id to get accruals for.
     * @param payPeriods - PeriodAccSummaries will be valid at the start of each period in this list.
     * @return TreeMap<PayPeriod, PeriodAccSummary>
     * @throws AccrualException
     */
    public TreeMap<PayPeriod, PeriodAccSummary> getAccruals(int empId, List<PayPeriod> payPeriods) throws AccrualException;
}
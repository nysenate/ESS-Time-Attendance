package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.AccrualHistory;
import gov.nysenate.seta.model.accrual.AccrualInfo;
import gov.nysenate.seta.model.period.PayPeriod;

import java.util.List;

/**
 * Data access layer for retrieving and computing accrual information
 * (e.g personal hours, vacation hours, etc).
 */
public interface AccrualDao extends BaseDao
{
    /**
     * Retrieve accrual usage info for the given employee id and a specific date.
     * @param empId int - Employee id
     * @param payPeriod Date - Pay period to get accrual for
     * @return AccrualInfo if found, throws AccrualException otherwise.
     * @throws  AccrualException
     */
    public AccrualInfo getAccuralInfo(int empId, PayPeriod payPeriod) throws AccrualException;

    /**
     * Retrieve a history of accruals for the given employee and the list of dates.
     * @param empId int - Employee id
     * @param payPeriods List<PayPeriod> - Attendance pay period dates to build history from
     * @return AccrualHistory
     * @throws AccrualException
     */
    public AccrualHistory getAccrualHistory(int empId, List<PayPeriod> payPeriods) throws AccrualException;

}

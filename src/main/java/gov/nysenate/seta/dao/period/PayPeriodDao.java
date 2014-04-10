package gov.nysenate.seta.dao.period;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.exception.PayPeriodException;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;

import java.util.Date;
import java.util.List;

/**
 * Data access layer for providing basic pay period information.
 */
public interface PayPeriodDao extends BaseDao
{
    /**
     * Retrieve the pay period of the given type that has a date range that either ends on or
     * contains the given 'date'.
     * @param type PayPeriodType - The type of pay period.
     * @param date Date
     * @return PayPeriod
     * @throws PayPeriodException - PayPeriodNotFoundEx if no matches were found.
     */
    public PayPeriod getPayPeriod(PayPeriodType type, Date date) throws PayPeriodException;

    /**
     * Retrieves the pay periods of the given type that are either between or include the start
     * and end date range.
     * @param type PayPeriodType - The type of pay period.
     * @param startDate Date
     * @param endDate Date
     * @return List<PayPeriod>
     */
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Date startDate, Date endDate);

    /**
     * Attendance years are closed out and finalized at some point after the year has ended. The pay periods
     * that belong to an open year are of interest because the employee can still submit attendance records
     * for them. The date at which a year closes out is specific to an employee and therefore the empId is
     * required.
     * @param empId int - Employee id
     * @param endDate Date - The retrieved pay periods will have a range before or during this date.
     * @return List<PayPeriod> - Ordered by most recent pay periods first.
     */
    public List<PayPeriod> getOpenAttendancePayPeriods(int empId, Date endDate);
}

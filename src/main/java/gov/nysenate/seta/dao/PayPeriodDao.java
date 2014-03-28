package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.PayPeriod;
import gov.nysenate.seta.model.PayPeriodType;
import gov.nysenate.seta.model.exception.PayPeriodException;

import java.util.Date;
import java.util.List;

public interface PayPeriodDao extends BaseDao
{
    /**
     *
     * @param type
     * @param date
     * @return
     * @throws PayPeriodException
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

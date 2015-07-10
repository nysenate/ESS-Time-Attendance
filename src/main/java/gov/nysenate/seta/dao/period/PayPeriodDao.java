package gov.nysenate.seta.dao.period;

import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.exception.PayPeriodException;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access layer for providing basic pay period information.
 */
public interface PayPeriodDao extends BaseDao
{
    /**
     * Retrieve the pay period of the given type that overlaps on the given 'date'.
     *
     * @param type PayPeriodType - The type of pay period.
     * @param date LocalDate - Date to fetch an overlapping pay period for
     * @return PayPeriod
     * @throws PayPeriodException - PayPeriodNotFoundEx if no matches were found.
     */
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date) throws PayPeriodException;

    /**
     * Retrieves the pay periods of the given type within the given dateRange. The returned pay periods
     * will either be contained within or overlap with the dateRange.
     *
     * @param type PayPeriodType - The type of pay period.
     * @param dateRange Range<LocalDate> - The start and end date range.
     * @param dateOrder SortOrder - Order by the start date of the pay period
     * @return List<PayPeriod>
     */
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder);

    /**
     * Attendance years are closed out and finalized at some point after the year has ended. The pay periods
     * that belong to an open year are of interest because the employee can still submit attendance records
     * for them. The date at which a year closes out is specific to an employee and therefore the empId is
     * required.
     *
     * @param empId int - Employee id
     * @param endDate LocalDate - The retrieved pay periods will have a range before or during this date.
     * @param dateOrder SortOrder - Order by the start date of the pay period
     * @return List<PayPeriod>
     */
    public List<PayPeriod> getOpenAttendancePayPeriods(int empId, LocalDate endDate, SortOrder dateOrder);
}
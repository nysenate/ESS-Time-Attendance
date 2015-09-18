package gov.nysenate.seta.service.attendance;

import gov.nysenate.common.WorkInProgress;
import gov.nysenate.seta.model.period.PayPeriod;

import java.util.Collection;
import java.util.Set;

/**
 * A service that is responsible for generating time records and ensuring that active time records contain valid data
 */
@WorkInProgress(author = "Sam", since = "2015/09/15", desc = "building and testing time record generation methods")
public interface TimeRecordManager {

    /**
     * Ensure that the given employee has records that cover the given pay periods
     *  ensures that all records covering the pay periods contain correct and up to date employee information
     * @param empId int - employee id
     * @param payPeriods Collection<PayPeriod> - pay periods
     * @return int - the number of records created/modified
     */
    public int ensureRecords(int empId, Collection<PayPeriod> payPeriods);

    /**
     * Ensure that all active employees have up to date, correct records for all active pay periods in the current year
     * @see #ensureRecords(int, Collection)
     */
    public void ensureAllActiveRecords();
}

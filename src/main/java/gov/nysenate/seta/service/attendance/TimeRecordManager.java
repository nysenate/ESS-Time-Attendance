package gov.nysenate.seta.service.attendance;

import gov.nysenate.common.WorkInProgress;
import gov.nysenate.seta.model.period.PayPeriod;

import java.util.Collection;

/**
 * A service that is responsible for generating time records and ensuring that time records
 *   and ensuring that all active time records are valid
 */
@WorkInProgress(author = "Sam", since = "2015/09/15", desc = "building and testing time record generation methods")
public interface TimeRecordManager {

    /**
     * Ensure that the given employee has records that cover the given pay periods
     *  ensures that all records covering the pay periods contain correct and up to date employee information
     * @param empId int - employee id
     * @param payPeriods Collection<PayPeriod> - pay periods
     */
    public void generateRecords(int empId, Collection<PayPeriod> payPeriods);
}

package gov.nysenate.seta.service;

import gov.nysenate.seta.model.accrual.AccrualInfo;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.personnel.Employee;

/**
 * Service interface to provide accrual related functionality.
 */
public interface AccrualService
{
    /**
     * Retrieves the AccrualInfo for the latest pay period given an employee id.
     * @param employeeId int
     * @return AccrualInfo if employeeId is valid.
     */
    AccrualInfo getCurrentAccruals(int employeeId);

    /**
     * Retrieves the AccrualInfo for the latest pay period given an Employee.
     * @param employee Employee
     * @return AccrualInfo if Employee is valid.
     */
    AccrualInfo getCurrentAccruals(Employee employee);

    /**
     * Retrieves the AccrualInfo for the specified PayPeriod given the employee id.
     * @param employeeId int
     * @param payPeriod PayPeriod
     * @return AccrualInfo if employee and pay period are valid.
     */
    AccrualInfo getAccrualsForPayPeriod(int employeeId, PayPeriod payPeriod);

    /**
     * Retrieves the AccrualInfo for the specified PayPeriod given the Employee.
     * @param employee Employee
     * @param payPeriod PayPeriod
     * @return AccrualInfo if employee and pay period are valid.
     */
    AccrualInfo getAccrualsForPayPeriod(Employee employee, PayPeriod payPeriod);
}

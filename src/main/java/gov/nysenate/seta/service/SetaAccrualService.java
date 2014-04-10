package gov.nysenate.seta.service;

import gov.nysenate.seta.model.accrual.AccrualInfo;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.personnel.Employee;
import org.springframework.stereotype.Service;

/**
 * Service implementation to provide senate accrual related functionality.
 */
@Service
public class SetaAccrualService implements AccrualService
{
    /** {@inheritDoc} */
    @Override
    public AccrualInfo getCurrentAccruals(int employeeId) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AccrualInfo getCurrentAccruals(Employee employee) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AccrualInfo getAccrualsForPayPeriod(int employeeId, PayPeriod payPeriod) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AccrualInfo getAccrualsForPayPeriod(Employee employee, PayPeriod payPeriod) {
        return null;
    }
}

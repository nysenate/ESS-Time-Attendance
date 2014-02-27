package gov.nysenate.seta.service;

import gov.nysenate.seta.dao.AccrualDao;
import gov.nysenate.seta.model.AccrualInfo;
import gov.nysenate.seta.model.Employee;
import gov.nysenate.seta.model.PayPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation to provide senate accrual related functionality.
 */
@Service
public class SenateAccrualService implements AccrualService
{
    @Autowired
    private AccrualDao accrualDao;

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

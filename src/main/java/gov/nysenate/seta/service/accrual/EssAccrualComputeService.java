package gov.nysenate.seta.service.accrual;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.accrual.AccrualDao;
import gov.nysenate.seta.dao.transaction.SqlEmpTransactionDao;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.TreeMap;

@Service
public class EssAccrualComputeService implements AccrualComputeService
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualComputeService.class);

    @Resource(name = "sqlAccrual")
    private AccrualDao accrualDao;

    @Autowired
    private SqlEmpTransactionDao transDao;

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public PeriodAccSummary getAccruals(int empId, PayPeriod payPeriod) throws AccrualException {
        verifyValidPayPeriod(payPeriod);
        LocalDate periodStartDate = payPeriod.getStartDate();
        TreeMap<PayPeriod, PeriodAccSummary> accSummMap =
            accrualDao.getPeriodAccrualSummaries(empId, periodStartDate.getYear(), periodStartDate);
        // If a period accrual usage record exists for the previous pay period, most of the work is done.
        if (!accSummMap.isEmpty() && accSummMap.lastKey().getEndDate().isEqual(periodStartDate.minusDays(1))) {
            logger.debug("Accrual summary found for previous pay period: {}", accSummMap.lastKey());
            return accSummMap.lastEntry().getValue();
        }

        return null;
    }

    @Override
    public TreeMap<PayPeriod, PeriodAccSummary> getAccruals(int empId, Range<PayPeriod> payPeriodRange) throws AccrualException {
        return null;
    }

    /** --- Internal Methods --- */

    private void verifyValidPayPeriod(PayPeriod payPeriod) {
        if (payPeriod == null) {
            throw new IllegalArgumentException("Supplied payPeriod cannot be null.");
        }
        else if (!payPeriod.getType().equals(PayPeriodType.AF)) {
            throw new IllegalArgumentException("Supplied payPeriod must be of type AF (Attendance Fiscal).");
        }
    }
}
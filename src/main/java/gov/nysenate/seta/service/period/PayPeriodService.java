package gov.nysenate.seta.service.period;

import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;

import java.time.LocalDate;
import java.util.List;

public interface PayPeriodService
{
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date);

    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder);
}

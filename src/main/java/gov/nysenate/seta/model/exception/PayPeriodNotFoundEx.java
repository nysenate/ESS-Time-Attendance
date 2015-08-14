package gov.nysenate.seta.model.exception;

import gov.nysenate.seta.model.period.PayPeriod;

import java.time.LocalDate;

public class PayPeriodNotFoundEx extends PayPeriodException
{
    public PayPeriodNotFoundEx() {}

    public PayPeriodNotFoundEx(String message) {
        super(message);
    }

    public PayPeriodNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }
}
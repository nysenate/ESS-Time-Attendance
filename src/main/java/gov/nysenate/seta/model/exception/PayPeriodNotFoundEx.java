package gov.nysenate.seta.model.exception;

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
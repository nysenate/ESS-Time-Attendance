package gov.nysenate.seta.model.exception;

public class PayPeriodException extends RuntimeException
{
    public PayPeriodException() {
    }

    public PayPeriodException(String message) {
        super(message);
    }

    public PayPeriodException(String message, Throwable cause) {
        super(message, cause);
    }
}

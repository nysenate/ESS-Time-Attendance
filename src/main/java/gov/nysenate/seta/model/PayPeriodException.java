package gov.nysenate.seta.model;

public class PayPeriodException extends Exception
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

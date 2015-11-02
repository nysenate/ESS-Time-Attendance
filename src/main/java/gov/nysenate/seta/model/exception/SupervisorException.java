package gov.nysenate.seta.model.exception;

public class SupervisorException extends RuntimeException
{
    public SupervisorException() {}

    public SupervisorException(String message) {
        super(message);
    }

    public SupervisorException(String message, Throwable cause) {
        super(message, cause);
    }
}

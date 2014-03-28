package gov.nysenate.seta.model.exception;

public class TransRecordException extends Exception
{
    public TransRecordException() {
        super();
    }

    public TransRecordException(String message) {
        super(message);
    }

    public TransRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}

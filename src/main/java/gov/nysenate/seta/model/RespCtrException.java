package gov.nysenate.seta.model;

public class RespCtrException extends Exception
{
    public RespCtrException() {
    }

    public RespCtrException(String message) {
        super(message);
    }

    public RespCtrException(String message, Throwable cause) {
        super(message, cause);
    }
}

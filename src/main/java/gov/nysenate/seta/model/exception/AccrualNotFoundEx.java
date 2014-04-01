package gov.nysenate.seta.model.exception;

/**
 * Created by brian heitner on 3/14/14.
 */
public class AccrualNotFoundEx extends AccrualException {
    public AccrualNotFoundEx() {}

    public AccrualNotFoundEx(String message) {
        super(message);
    }

    public AccrualNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }

}

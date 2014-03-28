package gov.nysenate.seta.model.exception;

public class RespCtrMultipleMatchesEx extends RespCtrException {

    public RespCtrMultipleMatchesEx() {
    }

    public RespCtrMultipleMatchesEx(String message) {
        super(message);
    }

    public RespCtrMultipleMatchesEx(String message, Throwable cause) {
        super(message, cause);
    }
}

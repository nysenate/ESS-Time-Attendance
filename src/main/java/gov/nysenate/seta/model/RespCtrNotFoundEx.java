package gov.nysenate.seta.model;

public class RespCtrNotFoundEx extends RespCtrException {

    public RespCtrNotFoundEx() {
    }

    public RespCtrNotFoundEx(String message) {
        super(message);
    }

    public RespCtrNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }
}

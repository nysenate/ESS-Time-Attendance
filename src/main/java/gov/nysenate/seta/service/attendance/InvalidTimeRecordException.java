package gov.nysenate.seta.service.attendance;

import gov.nysenate.seta.service.attendance.validators.InvalidTimeRecordCode;

public class InvalidTimeRecordException extends RuntimeException {

    private InvalidTimeRecordCode code;
    private Object errorData;

    public InvalidTimeRecordException(InvalidTimeRecordCode code, Object errorData, String message) {
        super(message);
        this.code = code;
        this.errorData = errorData;
    }

    public InvalidTimeRecordException(InvalidTimeRecordCode code, String message) {
        this(code, null, message);
    }

    public InvalidTimeRecordCode getCode() {
        return code;
    }

    public Object getErrorData() {
        return errorData;
    }
}

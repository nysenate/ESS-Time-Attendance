package gov.nysenate.seta.service.attendance.validation;

public enum TimeRecordErrorCode {

    INVALID_STATUS_CHANGE(1, "Attempt to change time record status in violation of time record life cycle")
    ;

    private int code;
    private String message;

    TimeRecordErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

package gov.nysenate.seta.service.attendance.validators;

public enum InvalidTimeRecordCode {

    INVALID_STATUS_CHANGE(1)    // Signifies a change in time record status that does not follow the valid life cycle
    ;

    private int code;

    InvalidTimeRecordCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

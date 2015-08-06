package gov.nysenate.seta.client.response.error;

public enum ErrorCode
{
    APPLICATION_ERROR(1, "An error occured while processing your request");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
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

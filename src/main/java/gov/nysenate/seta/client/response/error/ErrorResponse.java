package gov.nysenate.seta.client.response.error;

import gov.nysenate.seta.client.response.base.BaseResponse;

public class ErrorResponse extends BaseResponse
{
    protected ErrorCode errorCode;

    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.responseType = "error";
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

package gov.nysenate.seta.model.accrual;

public class AccrualException extends Exception
{
    protected AccrualType accrualType;

    public AccrualException(){}

    public AccrualException(AccrualType accrualType) {
        this.accrualType = accrualType;
    }

    public AccrualException(String message) {
        super(message);
    }

    public AccrualException(String message, Throwable cause) {
        super(message, cause);
    }

}

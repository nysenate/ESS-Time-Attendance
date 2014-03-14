package gov.nysenate.seta.model;

/**
 * Created by brian heitner on 3/14/14.
 */
public class AccrualException extends Exception {

    protected  AccrualType accrualType;

    public AccrualException(){}

    public AccrualException(AccrualType accrualType){
        this.accrualType = accrualType;
    }

    public AccrualException(String message){
        super(message);
    }

    public AccrualException(String message, Throwable cause){
        super(message, cause);
    }

}

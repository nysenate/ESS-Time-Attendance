package gov.nysenate.seta.model;

/**
 * Created by riken on 3/4/14.
 */
public class TimeRecordNotFoundException extends TimeRecordException{

    public TimeRecordNotFoundException(){}

    public TimeRecordNotFoundException(String message){
        super(message);
    }

    public TimeRecordNotFoundException(String message, Throwable cause){
        super(message, cause);
    }

}

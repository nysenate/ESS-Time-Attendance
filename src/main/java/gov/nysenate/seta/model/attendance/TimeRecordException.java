package gov.nysenate.seta.model.attendance;

/**
 * Created by riken on 3/4/14.
 */
public class TimeRecordException extends Exception {

    public TimeRecordException(){}

    public TimeRecordException(String message){
        super(message);
    }

    public  TimeRecordException(String message, Throwable cause){
        super(message, cause);
    }


}

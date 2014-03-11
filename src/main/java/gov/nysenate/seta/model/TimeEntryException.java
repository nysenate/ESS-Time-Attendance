package gov.nysenate.seta.model;

/**
 * Created by riken on 3/11/14.
 */
public class TimeEntryException extends Exception {
    public TimeEntryException(){}

    public TimeEntryException(String message){
        super(message);
    }

    public  TimeEntryException(String message, Throwable cause){
        super(message, cause);
    }
}

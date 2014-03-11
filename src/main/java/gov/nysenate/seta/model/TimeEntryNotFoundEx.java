package gov.nysenate.seta.model;

/**
 * Created by riken on 3/11/14.
 */
public class TimeEntryNotFoundEx extends TimeEntryException{
    public TimeEntryNotFoundEx(){}

    public TimeEntryNotFoundEx(String message){
        super(message);
    }

    public TimeEntryNotFoundEx(String message, Throwable cause){
        super(message, cause);
    }

}

package gov.nysenate.seta.model.exception;

import java.util.Date;

public class HolidayException extends Exception
{
    public HolidayException() {
        super();
    }

    public HolidayException(String message) {
        super(message);
    }

    public HolidayException(String message, Throwable cause) {
        super(message, cause);
    }
}

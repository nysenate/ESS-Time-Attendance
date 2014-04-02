package gov.nysenate.seta.model.exception;

import java.util.Date;

public class HolidayNotFoundForDateEx extends HolidayException
{
    public HolidayNotFoundForDateEx(Date date) {
        super("Holiday not found for date " + date.toString());
    }

    public HolidayNotFoundForDateEx(String message) {
        super(message);
    }

    public HolidayNotFoundForDateEx(String message, Throwable cause) {
        super(message, cause);
    }
}

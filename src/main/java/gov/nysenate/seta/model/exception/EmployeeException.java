package gov.nysenate.seta.model.exception;

public class EmployeeException extends Exception
{
    public EmployeeException() {}

    public EmployeeException(String message) {
        super(message);
    }

    public EmployeeException(String message, Throwable cause) {
        super(message, cause);
    }
}

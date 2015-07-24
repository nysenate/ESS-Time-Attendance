package gov.nysenate.seta.model.personnel;

public class EmployeeException extends RuntimeException
{
    public EmployeeException() {}

    public EmployeeException(String message) {
        super(message);
    }

    public EmployeeException(String message, Throwable cause) {
        super(message, cause);
    }
}

package gov.nysenate.seta.model;

public class EmployeeNotFoundEx extends EmployeeException
{
    public EmployeeNotFoundEx() {}

    public EmployeeNotFoundEx(String message) {
        super(message);
    }

    public EmployeeNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }
}

package gov.nysenate.seta.model.personnel;

public class EmployeeNotFoundEx extends EmployeeException
{
    int empId;

    public EmployeeNotFoundEx(int empId) {
        super("No employee was found with id " + empId);
        this.empId = empId;
    }

    public EmployeeNotFoundEx(String message) {
        super(message);
    }

    public EmployeeNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }

    public int getEmpId() {
        return empId;
    }
}

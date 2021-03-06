package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data access layer for retrieving employee information.
 */
public interface EmployeeDao extends BaseDao
{
    /**
     * Retrieve an Employee object based on the employee id.
     *
     * @param empId int - Employee id
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeException - EmployeeNotFoundEx if employee with given id was not found.
     */
    public Employee getEmployeeById(int empId) throws EmployeeException;

    /**
     * Retrieve an Employee object based on the employee email.
     *
     * @param email String - email
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeException - EmployeeNotFoundEx if employee with given email was not found.
     */
    public Employee getEmployeeByEmail(String email) throws EmployeeException;

    /**
     * Retrieves a Map of {emp id (Integer) -> Employee} given a collection of employee ids
     * to match against.
     *
     * @param empIds List<Integer> - List of employee ids
     * @return Map - {emp id (Integer) -> Employee} or empty map if no ids could be matched
     */
    public Map<Integer, Employee> getEmployeesByIds(List<Integer> empIds);

    /**
     * @return Employee info objects for all currently active employees
     */
    public Set<Employee> getActiveEmployees();

    /**
     * @return The ids for all currently active employees
     */
    public Set<Integer> getActiveEmployeeIds();

    /**
     * @return LocalDateTime - the date/time of the latest employee table update
     */
    public LocalDateTime getLastUpdateTime();

    /**
     * Get all employees with fields that were updated after the given date time
     * @param fromDateTime LocalDateTime
     * @return List<Employee>
     */
    public List<Employee> getUpdatedEmployees(LocalDateTime fromDateTime);
}

package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Employee;
import gov.nysenate.seta.model.exception.EmployeeException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data access layer for retrieving employee information.
 */
public interface EmployeeDao extends BaseDao
{
    /**
     * Retrieve an Employee object based on the employee id.
     * @param empId int - Employee id
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeException - EmployeeNotFoundEx if employee with given id was not found.
     */
    public Employee getEmployeeById(int empId) throws EmployeeException;

    /**
     * Retrieve an Employee object based on the employee email.
     * @param email String - email
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeException - EmployeeNotFoundEx if employee with given email was not found.
     */
    public Employee getEmployeeByEmail(String email) throws EmployeeException;

    /**
     * Retrieves a Map of employee id (Integer) -> Employee given a collection
     * of employee ids to match against.
     * @param empIds List<Integer> - List of employee ids
     * @return Map - employee id (Integer) -> Employee, throws EmployeeNotFoundEx if an
     *               an employee id in the list could not be matched.
     */
    public Map<Integer, Employee> getEmployeesByIds(List<Integer> empIds);

    /**
     * Return a list of all the ids for employees that are currently active.
     * @return List<Integer> of employee ids
     */
    public List<Integer> getActiveEmployeeIds();

    /**
     * Return a list of all the ids for employees that are active during the date range.
     * @param start Date
     * @param end Date
     * @return List<Integer> of employee ids
     */
    public List<Integer> getActiveEmployeesDuring(Date start, Date end);

    /**
     * Generates a Map of employee id -> Employee for all employees that are active during the
     * given date range.
     * @param start Date
     * @param end Date
     * @return Map of employee id (Integer) -> Employee
     */
    public Map<Integer, Employee> getActiveEmployeeMap(Date start, Date end);
}

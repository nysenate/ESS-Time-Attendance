package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.personnel.EmployeeException;
import gov.nysenate.seta.model.personnel.Employee;

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
     * Retrieves a Map of {emp id (Integer) -> Employee} given a collection of employee ids
     * to match against.
     * @param empIds List<Integer> - List of employee ids
     * @return Map - {emp id (Integer) -> Employee} or empty map if no ids could be matched
     */
    public Map<Integer, Employee> getEmployeesByIds(List<Integer> empIds);

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

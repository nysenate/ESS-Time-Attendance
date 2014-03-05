package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Employee;
import gov.nysenate.seta.model.EmployeeNotFoundEx;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EmployeeDao extends BaseDao
{
    /**
     * Retrieve an Employee object based on the employee id.
     * @param empId int - Employee id
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeNotFoundEx
     */
    public Employee getEmployeeById(int empId) throws EmployeeNotFoundEx;

    /**
     * Retrieve an Employee object based on the employee id and a specific date. The
     * Employee returned will reflect the state of the employee up until the given date.
     * @param empId int - Employee id
     * @param date Date - Snapshot date
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeNotFoundEx
     */
    public Employee getEmployeeByIdDuring(int empId, Date date) throws EmployeeNotFoundEx;

    /**
     * Retrieve an Employee object based on the employee email.
     * @param email String - email
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeNotFoundEx
     */
    public Employee getEmployeeByEmail(String email) throws EmployeeNotFoundEx;

    /**
     * Retrieves a Map of employee id (Integer) -> Employee given a collection
     * of employee ids to match against.
     * @param empIds List<Integer> - List of employee ids
     * @return Map - employee id (Integer) -> Employee, throws EmployeeNotFoundEx if an
     *               an employee id in the list could not be matched.
     * @throws EmployeeNotFoundEx
     */
    public Map<Integer, Employee> getEmployeesByIds(List<Integer> empIds) throws EmployeeNotFoundEx;

    /**
     * Retrieves all employees that are actively employed.
     * @return List<Employee>
     */
    public List<Employee> getActiveEmployees();

    /**
     * Generates a Map of employee id -> Employee.
     * @return Map of employee id (Integer) -> Employee
     */
    public Map<Integer, Employee> getActiveEmployeeMap();
}

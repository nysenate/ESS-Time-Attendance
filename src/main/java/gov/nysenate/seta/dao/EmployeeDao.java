package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Employee;
import gov.nysenate.seta.model.EmployeeNotFoundEx;

import java.util.List;
import java.util.Map;

public interface EmployeeDao extends BaseDao
{
    /**
     * Retrieve an Employee object based on the employee id.
     * @param empId int - Employee id
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     */
    public Employee getEmployeeById(int empId) throws EmployeeNotFoundEx;

    /**
     * Retrieve an Employee object based on the employee email.
     * @param uid String - email
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     */
    public Employee getEmployeeByEmail(String uid) throws EmployeeNotFoundEx;

    /**
     *
     * @return
     */
    public List<Employee> getEmployees();

    /**
     *
     * @return
     */
    public Map<Integer, Employee> getEmployeeIdMap();
}

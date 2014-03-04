package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.SupervisorException;

import java.util.Date;

public interface SupervisorDao extends BaseDao
{
    /**
     * Indicates whether the given supId is currently a T&A supervisor.
     * @param empId int - employee id
     * @return boolean - true if 'empId' is currently a supervisor, false otherwise
     */
    public boolean isSupervisor(int empId);

    /**
     * Indicates whether the given supId is/was a T&A supervisor during the given date.
     * @param empId int - employee id
     * @param date Date - check for supervisor status during this date
     * @return boolean - true if 'empId' is a supervisor during 'date', false otherwise
     */
    public boolean isSupervisor(int empId, Date date);

    /**
     * Retrieve the current T&A supervisor id for the given employee id.
     * @param empId int - Employee id
     * @return int - Supervisor id
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public int getSupervisorIdForEmp(int empId) throws SupervisorException;


    /*
    public Supervisor getSupervisorForEmp(int empId);

    public Supervisor getSupervisorForEmpDuring(int empId, Date start, Date end);

    public Supervisor getSupervisorForEmpDuring(int empId, PayPeriod payPeriod);

    public List<Employee> getEmployeesForSupervisor(int supId);

    public List<Employee> getEmployeesForSupervisorDuring(int supId, PayPeriod payPeriod);

    public List<Employee> getEmployeesForSupervisorDuring(int supId, Date start, Date end);
      */
}

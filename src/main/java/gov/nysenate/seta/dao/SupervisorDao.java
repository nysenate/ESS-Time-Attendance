package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Employee;
import gov.nysenate.seta.model.PayPeriod;
import gov.nysenate.seta.model.Supervisor;

import java.util.Date;
import java.util.List;

public interface SupervisorDao extends BaseDao
{
    public Supervisor getSupervisorById(int supId);

    public Supervisor getSupervisorForEmp(int empId);

    public Supervisor getSupervisorForEmpDuring(int empId, Date start, Date end);

    public Supervisor getSupervisorForEmpDuring(int empId, PayPeriod start, PayPeriod end);

    public List<Employee> getEmployeesForSupervisor(int supId);

    public List<Employee> getEmployeesForSupervisorDuring(int supId, PayPeriod start, PayPeriod end);

    public List<Employee> getEmployeesForSupervisorDuring(int supId, Date start, Date end);

}

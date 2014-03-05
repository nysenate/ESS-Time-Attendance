package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Supervisor;
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

    /**
     * Retrieve the effective T&A supervisor id for the given employee id during the supplied date.
     * @param empId int - Employee id
     * @param date Date - Date during which the supervisor is active
     * @return int - Supervisor id
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public int getSupervisorIdForEmp(int empId, Date date) throws SupervisorException;

    /**
     * Retrieves a Supervisor object for the given supId for the most current date range.
     * @param supId int - employee id for supervisor
     * @return Supervisor for matching supId
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public Supervisor getSupervisor(int supId) throws SupervisorException;

    /**
     * Retrieves a Supervisor object for the given supId during a period containing the supplied date.
     * @param supId int - employee id for supervisor
     * @param date Date - Date during which the supervisor is active
     * @return Supervisor for matching supId
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public Supervisor getSupervisor(int supId, Date date) throws SupervisorException;
}

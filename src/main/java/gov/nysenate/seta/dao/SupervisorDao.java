package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;

import java.util.Date;

/**
 * Data access layer for retrieving T&A supervisor info as well as setting overrides.
 */
public interface SupervisorDao extends BaseDao
{
    /**
     * Retrieves a Supervisor object for the given supId.
     * @param supId int - employee id for supervisor
     * * @return Supervisor for matching supId
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public Supervisor getSupervisor(int supId) throws SupervisorException;

    /**
     * Indicates whether the given supId is/was a T&A supervisor during the given dates.
     * @param empId int - employee id
     * @param start Date - start of date range
     * @param end Date - end of date range
     * @return boolean - true if 'empId' had subordinates during the date range and was thus
     *                   a supervisor.
     */
    public boolean isSupervisor(int empId, Date start, Date end);

    /**
     * Retrieve the effective T&A supervisor id for the given employee id during the supplied date.
     * @param empId int - Employee id
     * @param date Date - Point in time to get the supervisor for
     * @return int - Supervisor id
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public int getSupervisorIdForEmp(int empId, Date date) throws SupervisorException;

    /**
     * Computes and returns the SupervisorChain for the given supId during the specified date.
     * @param supId int - Supervisor id
     * @param date Date - Date to compute the supervisor chain for
     * @return SupervisorChain for matching supId
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public SupervisorChain getSupervisorChain(int supId, Date date) throws SupervisorException;

    /**
     * Retrieves the collection of employees that are managed by the given supervisor between the start and
     * end dates. For the purposes of T&A, a supervisor can also manage the records of employees of another
     * supervisor if an override was established for the given date range.
     * @param supId int - Supervisor id
     * @param start Date - Start of date range (typically the first day of a certain pay period)
     * @param end Date - End of date range (typically the last day of a certain pay period)
     * @return SupervisorEmpGroup if successful, throws SupervisorException otherwise
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Date start, Date end) throws SupervisorException;
}
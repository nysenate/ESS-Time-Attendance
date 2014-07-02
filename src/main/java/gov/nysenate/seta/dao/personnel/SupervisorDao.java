package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.SupervisorChain;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;

import java.util.Date;

/**
 * Data access layer for retrieving T&A supervisor info as well as setting overrides.
 */
public interface SupervisorDao extends BaseDao
{
    /**
     * Indicates whether the given supId is/was a T&A supervisor during the given dates.
     * @param empId int - employee id
     * @param start Date - start of date range
     * @param end Date - end of date range
     * @return boolean - true if 'empId' had subordinates during the date range and was thus
     *                   a supervisor, false otherwise.
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
     * Computes and returns a listing of the hierarchy of supervisors starting from the given empId
     * during the specified date.
     * @param empId int - Employee id
     * @param date Date - Date to compute the supervisor chain for
     * @return SupervisorChain for matching empId
     * @throws SupervisorException - SupervisorNotFoundEx if a supervisor could not be found for any
     *                                                    employee that is encountered in the chain.
     */
    public SupervisorChain getSupervisorChain(int empId, Date date) throws SupervisorException;

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

    /**
     * Sets an override so that 'ovrSupId' can have access to the primary employees of 'supId' during the
     * given date range.
     * @param supId int - The supervisor granting the override.
     * @param ovrSupId int - The supervisor receiving the override.
     * @param start Date - The start date, set to null if override is effective immediately.
     * @param end Date - The end date, set to null if override is effective permanently.
     * @throws SupervisorException - SupervisorNotFoundEx if either supervisor could not be found
     *                               SupervisorNotInChainEx if 'ovrSupId' is not in 'supId's' chain
     */
    public void setSupervisorOverride(int supId, int ovrSupId, Date start, Date end) throws SupervisorException;
}
package gov.nysenate.seta.dao.personnel;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.SupervisorChain;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;

import java.time.LocalDate;
import java.util.Date;

/**
 * Data access layer for retrieving T&A supervisor info as well as setting overrides.
 */
public interface SupervisorDao extends BaseDao
{
    /**
     * Indicates whether the given supId is/was a T&A supervisor during the given dates.
     *
     * @param empId int - employee id
     * @param dateRange Range<LocalDate> - date range
     * @return boolean - true if 'empId' had subordinates during the date range and was thus
     *                   a supervisor, false otherwise.
     */
    public boolean isSupervisor(int empId, Range<LocalDate> dateRange);

    /**
     * Retrieve the effective T&A supervisor id for the given employee id during the supplied date.
     *
     * @param empId int - Employee id
     * @param date LocalDate - Point in time to get the supervisor for
     * @return int - Supervisor id
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public int getSupervisorIdForEmp(int empId, LocalDate date) throws SupervisorException;

    /**
     * Computes and returns a listing of the hierarchy of supervisors starting from the given empId
     * during the specified date.
     *
     * @param empId int - Employee id
     * @param date LocalDate - Date to compute the supervisor chain for
     * @return SupervisorChain for matching empId
     * @throws SupervisorException - SupervisorNotFoundEx if a supervisor could not be found for any
     *                                                    employee that is encountered in the chain.
     */
    public SupervisorChain getSupervisorChain(int empId, LocalDate date) throws SupervisorException;

    /**
     * Retrieves the collection of employees that are managed by the given supervisor during any time in
     * the supplied date range. This group will also contain any overrides that were active during that time.
     *
     * @param supId int - Supervisor id
     * @param dateRange Range<LocalDate> - The date range to filter by
     * @return SupervisorEmpGroup if successful, throws SupervisorException otherwise
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException;

    /**
     * Sets an override so that 'ovrSupId' can have access to the primary employees of 'supId' during the
     * given date range.
     *
     * @param supId int - The supervisor granting the override.
     * @param ovrSupId int - The supervisor receiving the override.
     * @param dateRange Range<LocalDate> - The date range to set override for.
     * @throws SupervisorException - SupervisorNotFoundEx if either supervisor could not be found
     *                               SupervisorNotInChainEx if 'ovrSupId' is not in 'supId's' chain
     */
    public void setSupervisorOverride(int supId, int ovrSupId, Range<LocalDate> dateRange) throws SupervisorException;
}
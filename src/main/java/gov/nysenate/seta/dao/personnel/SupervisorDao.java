package gov.nysenate.seta.dao.personnel;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.*;

import java.time.LocalDate;
import java.util.List;

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
    boolean isSupervisor(int empId, Range<LocalDate> dateRange);

    /**
     * Retrieves any supervisor ids that are designated to be included or excluded from the
     * given employee's supervisor chain.
     *
     * @param empId int
     * @return SupervisorChainAlteration
     */
    SupervisorChainAlteration getSupervisorChainAlterations(int empId);

    /**
     * Retrieves the collection of employees that are managed by the given supervisor during any time in
     * the supplied date range. This group will also contain any overrides that were active during that time.
     *
     * @param supId int - Supervisor id
     * @param dateRange Range<LocalDate> - The date range to filter by
     * @return SupervisorEmpGroup if successful, throws SupervisorException otherwise
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException;

    /**
     * Retrieves a list of active supervisor overrides for the given supervisor.
     * @param supId int - Supervisor id
     * @param type SupGrantType -
     *             if SupGrantType is GRANTEE, any supervisors that have granted an override to 'supId' will be fetched.
     *             if SupGrantType is GRANTER, any supervisors 'supId' has granted permissions to will be fetched.
     * @return List<SupervisorOverride>
     * @throws SupervisorException
     */
    List<SupervisorOverride> getSupervisorOverrides(int supId, SupGrantType type) throws SupervisorException;

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
    void setSupervisorOverride(int supId, int ovrSupId, Range<LocalDate> dateRange) throws SupervisorException;
}
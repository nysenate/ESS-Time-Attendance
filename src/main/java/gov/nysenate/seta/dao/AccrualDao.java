package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.AccrualInfo;
import gov.nysenate.seta.model.AccrualNotFoundEx;

import java.util.Date;

public interface AccrualDao extends BaseDao
{
    /**
     * Retrieve an AccrualInfo object based on the employee id.
     * @param empId int - Employee id
     * @return AccrualInfo if found, throws AccrualNotFoundEx otherwise.
     * @throws AccrualNotFoundEx
     */
    public AccrualInfo getAccuralInfo(int empId) throws  AccrualNotFoundEx;

    /**
     * Retrieve an AccrualInfo object based on the employee id and a specific date. The
     * AccrualInfo returned will reflect the state of the employee accruals up until the given date.
     * @param empId int - Employee id
     * @param date Date - Snapshot date
     * @return AccrualInfo if found, throws AccuralNotFoundEx otherwise.
     * @throws  AccrualNotFoundEx
     */
    public AccrualInfo getAccuralInfo(int empId, Date date) throws  AccrualNotFoundEx;

}

package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.AccrualHistory;
import gov.nysenate.seta.model.AccrualInfo;
import gov.nysenate.seta.model.exception.AccrualException;
import gov.nysenate.seta.model.exception.AccrualNotFoundEx;

import java.util.Date;
import java.util.List;

/**
 * Data access layer for retrieving and computing accrual information
 * (e.g personal hours, vacation hours, etc).
 */
public interface AccrualDao extends BaseDao
{
    /**
     * Retrieve accrual usage info for the given employee id and a specific date.
     * @param empId int - Employee id
     * @param date Date - Snapshot date
     * @return AccrualInfo if found, throws AccrualException otherwise.
     * @throws  AccrualException
     */
    public AccrualInfo getAccuralInfo(int empId, Date date) throws AccrualException;

    /**
     * Retrieve a history of accruals for the given employee and the list of dates.
     * @param empId int - Employee id
     * @param dates Date - Snapshot dates to build history from
     * @return AccrualHistory
     * @throws AccrualException
     */
    public AccrualHistory getAccrualHistory(int empId, List<Date> dates) throws AccrualException;

}

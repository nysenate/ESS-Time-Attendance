package gov.nysenate.seta.dao.allowances;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.allowances.OldAllowanceUsage;

/**
 * Data access layer for retrieving and computing allowance information
 * (e.g temporary employee yearly allowances, hours used).
 */
public interface AllowanceDao extends BaseDao
{
    public OldAllowanceUsage getAllowanceUsage(int empId, int year);
}
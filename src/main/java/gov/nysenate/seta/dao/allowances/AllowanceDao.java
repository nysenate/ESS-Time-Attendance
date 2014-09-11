package gov.nysenate.seta.dao.allowances;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.allowances.AllowanceUsage;
import gov.nysenate.seta.model.transaction.AuditHistory;

/**
 * Data access layer for retrieving and computing allowance information
 * (e.g temporary employee yearly allowances, hours used).
 */
public interface AllowanceDao extends BaseDao
{
    public AllowanceUsage getAllowanceUsage(int empId, int year, AuditHistory auditHistory);
}
package gov.nysenate.seta.dao.payroll;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.payroll.Deduction;

import java.time.LocalDate;
import java.util.List;

public interface DeductionDao extends BaseDao
{
    /**
     * Get all deductions applied to a paycheck.
     * @param empId Employee Id
     * @param checkDate Date paycheck was issued.
     * @return
     */
    List<Deduction> getDeductionsForPaycheck(int empId, LocalDate checkDate);
}

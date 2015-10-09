package gov.nysenate.seta.service.payroll;

import gov.nysenate.seta.model.payroll.Paycheck;

import java.util.List;

public interface PaycheckService
{
    List<Paycheck> getEmployeePaychecksForYear(int empId, int year);
}

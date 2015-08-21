package gov.nysenate.seta.service.accrual;

import gov.nysenate.seta.model.accrual.AnnualAccSummary;

import java.util.TreeMap;

public interface AccrualInfoService
{
    TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear);
}

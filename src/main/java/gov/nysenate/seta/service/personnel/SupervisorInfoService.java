package gov.nysenate.seta.service.personnel;

import com.google.common.collect.Range;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;

import java.time.LocalDate;

public interface SupervisorInfoService
{
    boolean isSupervisorDuring(Range<LocalDate> dateRange);

    SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException;
}

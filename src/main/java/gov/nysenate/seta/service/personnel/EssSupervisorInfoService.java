package gov.nysenate.seta.service.personnel;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.personnel.SupervisorDao;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.SupervisorEmpGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EssSupervisorInfoService implements SupervisorInfoService
{
    @Autowired private SupervisorDao supervisorDao;

    @Override
    public boolean isSupervisorDuring(Range<LocalDate> dateRange) {
        return false;
    }

    @Override
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException {
        return supervisorDao.getSupervisorEmpGroup(supId, dateRange);
    }
}
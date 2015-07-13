package gov.nysenate.seta.service.attendance;

import gov.nysenate.seta.dao.accrual.AccrualDao;
import gov.nysenate.seta.dao.accrual.HoursDao;
import gov.nysenate.seta.dao.allowances.AllowanceDao;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.dao.personnel.HolidayDao;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.attendance.TimeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RemoteTimeRecordValidationService implements TimeRecordValidationService {

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    AccrualDao accrualDao;

    @Autowired
    AllowanceDao allowanceDao;

    @Autowired
    HolidayDao holidayDao;

    @Autowired
    HoursDao hoursDao;

    @Override
    public void validateTimeRecord(TimeRecord timeRecord) throws InvalidTimeRecordException {

    }

    private void validateAccruals(TimeRecord timeRecord) throws InvalidTimeRecordException {
        PeriodAccSummary pacSum = accrualDao.getPeriodAccrualSummaries(timeRecord.getEmployeeId(),
                                                timeRecord.getPayPeriod().getEndDate().getYear(),
                                                timeRecord.getPayPeriod().getEndDate()).get(timeRecord.getPayPeriod());
    }

    private void validateAllowances(TimeRecord timeRecord) throws InvalidTimeRecordException {

    }

    private void validateStatus(TimeRecord timeRecord) throws InvalidTimeRecordException {

    }
}

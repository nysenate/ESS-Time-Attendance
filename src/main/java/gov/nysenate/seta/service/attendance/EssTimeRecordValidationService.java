package gov.nysenate.seta.service.attendance;

import com.google.common.collect.ImmutableList;
import gov.nysenate.seta.dao.attendance.TimeRecordDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.service.attendance.validators.LifeCycleTRV;
import gov.nysenate.seta.service.attendance.validators.TimeRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class EssTimeRecordValidationService implements TimeRecordValidationService {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordValidationService.class);

    @Autowired private TimeRecordDao timeRecordDao;

    @Autowired private LifeCycleTRV lifeCycleTRV;

    private ImmutableList<TimeRecordValidator> timeRecordValidators;

    @PostConstruct
    public void init() {
        timeRecordValidators = ImmutableList.<TimeRecordValidator>builder()
                .add(lifeCycleTRV)
                // TODO: ADD SOME more VALIDATORS
                .build();
    }

    @Override
    public void validateTimeRecord(TimeRecord record) throws InvalidTimeRecordException {
        TimeRecord prevState = timeRecordDao.getTimeRecord(record.getTimeRecordId());
        timeRecordValidators.stream()
                .filter(validator -> validator.isApplicable(record, prevState))
                .forEach(validator -> validator.checkTimeRecord(record, prevState));
    }
}

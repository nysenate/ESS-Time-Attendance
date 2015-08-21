package gov.nysenate.seta.service.personnel;

import com.google.common.collect.RangeSet;
import gov.nysenate.seta.BaseTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class EmployeeInfoServiceTests extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeInfoServiceTests.class);

    @Autowired EmployeeInfoService employeeInfoService;

    @Test
    public void activeDatesTest() {
        RangeSet<LocalDate> activeDates = employeeInfoService.getEmployeeActiveDatesService(1719);
        logger.info("{}", activeDates.asRanges());
    }
}

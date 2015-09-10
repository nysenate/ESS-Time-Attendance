package gov.nysenate.seta.service.personnel;

import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.personnel.SupervisorOverride;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;

public class EssCachedSupervisorInfoServiceTests extends BaseTests
{
    @Autowired EssCachedSupervisorInfoService supInfoService;

    @Test
    @Rollback(value = false)
    public void testUpdateSupervisorOverride() throws Exception {
        SupervisorOverride ovr = new SupervisorOverride();
        ovr.setGranterSupervisorId(9896);
        ovr.setGranteeSupervisorId(7048);
        ovr.setActive(true);
        ovr.setStartDate(Optional.of(LocalDate.now()));
        ovr.setEndDate(Optional.of(LocalDate.now()));
        supInfoService.updateSupervisorOverride(ovr);
    }
}
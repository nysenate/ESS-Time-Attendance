package gov.nysenate.seta.dao.payroll;

import gov.nysenate.seta.model.exception.RespCtrException;
import gov.nysenate.seta.model.personnel.ResponsibilityCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SqlResponsibilityCtrDao implements ResponsibilityCtrDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlResponsibilityCtrDao.class);

    /** {@inheritDoc} */
    @Override
    public ResponsibilityCenter getRespCtr(String agencyCode, String respCtrHead) throws RespCtrException {
        return null;
    }

}

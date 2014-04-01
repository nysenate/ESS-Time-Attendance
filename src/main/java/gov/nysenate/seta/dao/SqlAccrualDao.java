package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.AccrualHistory;
import gov.nysenate.seta.model.AccrualInfo;
import gov.nysenate.seta.model.exception.AccrualException;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SqlAccrualDao extends SqlBaseDao implements AccrualDao
{
    /** {@inheritDoc} */
    @Override
    public AccrualInfo getAccuralInfo(int empId, Date date) throws AccrualException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AccrualHistory getAccrualHistory(int empId, List<Date> dates) throws AccrualException {
        return null;
    }
}

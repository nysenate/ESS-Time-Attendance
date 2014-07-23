package gov.nysenate.seta.dao.allowances;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.allowances.TEHours;
import gov.nysenate.seta.model.transaction.AuditHistory;

import java.util.ArrayList;

/**
 * Created by heitner on 7/23/2014.
 */
public interface TEHoursDao extends BaseDao {

    public ArrayList <TEHours> getTEHours(int empId, int year, AuditHistory auditHistory);
}

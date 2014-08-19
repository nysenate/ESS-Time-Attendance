package gov.nysenate.seta.dao.allowances;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.allowances.TEHours;
import gov.nysenate.seta.model.transaction.AuditHistory;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by heitner on 7/23/2014.
 */
public interface TEHoursDao extends BaseDao {

    public ArrayList<TEHours> getTEHours(int empId, int year);

    public  ArrayList<TEHours> getTEHours(int empId, Date beginDate, Date endDate);

    public TEHours sumTEHours(ArrayList<TEHours> teHourses);

}

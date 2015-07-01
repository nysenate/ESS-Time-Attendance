package gov.nysenate.seta.dao.allowances;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.allowances.TEHours;

import java.util.Date;
import java.util.List;

public interface TEHoursDao extends BaseDao {

    public List<TEHours> getTEHours(int empId, int year);

    public TEHours sumTEHours(List<TEHours> teHourses);

    public List<TEHours> getTEHours(int empId, Date beginDate, Date endDate);

}

package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.exception.HolidayNotFoundForDateEx;
import gov.nysenate.seta.model.payroll.Holiday;

import java.util.Date;
import java.util.List;

/**
 * Data access layer for retrieving holiday dates.
 */
public interface HolidayDao extends BaseDao
{
    /**
     * Returns a holiday if it exists for the given date. Throws exception otherwise.
     * @param date Date
     * @return Holiday
     * @throws HolidayNotFoundForDateEx if a holiday was not found for given date.
     */
    public Holiday getHoliday(Date date) throws HolidayNotFoundForDateEx;

    /**
     * Retrieves a list of all the non-questionable holidays that occur between the given dates inclusively in order
     * of earliest first.
     * @param startDate Start date range
     * @param endDate End date range
     * @return List<Holiday>
     */
    public List<Holiday> getHolidays(Date startDate, Date endDate);

    /**
     * Retrieves a list of all the holidays that occur between the given dates inclusively in order
     * of earliest first.
     * @param startDate Start date range
     * @param endDate End date range
     * @param questionableHolidays Include questionable holidays
     * @return List<Holiday>
     */
    public List<Holiday> getHolidays(Date startDate, Date endDate, boolean questionableHolidays);

}

package gov.nysenate.seta.dao.personnel;

import com.google.common.collect.Range;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.exception.HolidayNotFoundForDateEx;
import gov.nysenate.seta.model.payroll.Holiday;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access layer for retrieving holiday dates.
 */
public interface HolidayDao extends BaseDao
{
    /**
     * Returns a holiday if it exists for the given date. Throws exception otherwise.
     *
     * @param date Date
     * @return Holiday
     * @throws HolidayNotFoundForDateEx if a holiday was not found for given date.
     */
    public Holiday getHoliday(LocalDate date) throws HolidayNotFoundForDateEx;

    /**
     * Retrieves a list of all the non-questionable holidays that occur within the given range.
     *
     * @param dateRange Range<LocalDate> - The date range to search.
     * @param dateOrder SortOrder - Order the results by date.
     * @return List<Holiday>
     */
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, SortOrder dateOrder);

    /**
     * Retrieves a list of all the holidays that occur between the given dates inclusively in order
     * of earliest first.
     *
     * @param dateRange Range<LocalDate> - The date range to search.
     * @param includeQuestionable boolean - Include questionable holidays
     * @param dateOrder SortOrder - Order the results by date.
     * @return List<Holiday>
     */
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, boolean includeQuestionable, SortOrder dateOrder);
}
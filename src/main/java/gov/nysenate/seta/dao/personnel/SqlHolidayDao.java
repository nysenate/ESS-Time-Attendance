package gov.nysenate.seta.dao.personnel;

import com.google.common.collect.Range;
import gov.nysenate.seta.dao.base.OrderBy;
import gov.nysenate.seta.dao.base.SortOrder;
import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.base.SqlQueryUtils;
import gov.nysenate.seta.dao.personnel.mapper.HolidayRowMapper;
import gov.nysenate.seta.model.exception.HolidayNotFoundForDateEx;
import gov.nysenate.seta.model.payroll.Holiday;
import gov.nysenate.seta.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static gov.nysenate.seta.util.DateUtils.endOfDateRange;
import static gov.nysenate.seta.util.DateUtils.startOfDateRange;
import static gov.nysenate.seta.util.DateUtils.toDate;

/** {@inheritDoc} */
@Repository
public class SqlHolidayDao extends SqlBaseDao implements HolidayDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlHolidayDao.class);

    protected static final String GET_HOLIDAY_SQL =
        "SELECT * FROM SASSHD17691 WHERE DTHOLIDAY = :date";

    protected static final String GET_HOLIDAYS_SQL =
        "SELECT * FROM SASSHD17691 WHERE DTHOLIDAY BETWEEN :startDate AND :endDate";

    protected static final String GET_NON_QUESTIONABLE_HOLIDAYS_SQL =
        GET_HOLIDAYS_SQL + " AND cdquest = 'N'";

    /** {@inheritDoc} */
    @Override
    public Holiday getHoliday(LocalDate date) throws HolidayNotFoundForDateEx {
        MapSqlParameterSource params = new MapSqlParameterSource("date", toDate(date));
        try {
            return remoteNamedJdbc.queryForObject(GET_HOLIDAY_SQL, params, new HolidayRowMapper(""));
        }
        catch (DataRetrievalFailureException ex) {
            throw new HolidayNotFoundForDateEx(date);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, SortOrder dateOrder) {
        return getHolidays(dateRange, false, dateOrder);
    }

    /** {@inheritDoc} */
    @Override
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, boolean includeQuestionable, SortOrder dateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("startDate", toDate(startOfDateRange(dateRange)))
            .addValue("endDate", toDate(endOfDateRange(dateRange)));
        String orderBy = SqlQueryUtils.getOrderByClause(new OrderBy("DTHOLIDAY", dateOrder));
        String holidaySql = (includeQuestionable) ? GET_HOLIDAYS_SQL : GET_NON_QUESTIONABLE_HOLIDAYS_SQL + orderBy;
        return remoteNamedJdbc.query(holidaySql, params, new HolidayRowMapper(""));
    }
}
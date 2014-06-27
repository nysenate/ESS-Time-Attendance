package gov.nysenate.seta.dao.personnel;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.dao.personnel.mapper.HolidayRowMapper;
import gov.nysenate.seta.model.exception.HolidayNotFoundForDateEx;
import gov.nysenate.seta.model.payroll.Holiday;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SqlHolidayDao extends SqlBaseDao implements HolidayDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlHolidayDao.class);

    protected static final String GET_HOLIDAY_SQL =
        "SELECT * FROM SASSHD17691 WHERE DTHOLIDAY = :date";

    protected static final String GET_HOLIDAYS_SQL =
        "SELECT * FROM SASSHD17691 WHERE DTHOLIDAY BETWEEN :startDate AND :endDate ORDER BY DTHOLIDAY";

    protected static final String GET_NON_QUESTIONABLE_HOLIDAYS_SQL =
            "SELECT * FROM SASSHD17691 WHERE DTHOLIDAY BETWEEN :startDate AND :endDate AND cdquest = 'N' ORDER BY DTHOLIDAY";

    /** {@inheritDoc} */
    @Override
    public Holiday getHoliday(Date date) throws HolidayNotFoundForDateEx {
        MapSqlParameterSource params = new MapSqlParameterSource("date", date);
        try {
            return remoteNamedJdbc.queryForObject(GET_HOLIDAY_SQL, params, new HolidayRowMapper(""));
        }
        catch (DataRetrievalFailureException ex) {
            throw new HolidayNotFoundForDateEx(date);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Holiday> getHolidays(Date startDate, Date endDate) {
            return getHolidays(startDate, endDate, false);
    }

    /** {@inheritDoc} */
    @Override
    public List<Holiday> getHolidays(Date startDate, Date endDate, boolean questionableHolidays) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", startDate);
        params.addValue("endDate", endDate);
        if (questionableHolidays) {
            return remoteNamedJdbc.query(GET_HOLIDAYS_SQL, params, new HolidayRowMapper(""));
        }
        else {
            return remoteNamedJdbc.query(GET_NON_QUESTIONABLE_HOLIDAYS_SQL, params, new HolidayRowMapper(""));
        }
    }
}

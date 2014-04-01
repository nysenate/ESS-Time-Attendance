package gov.nysenate.seta.dao;

import gov.nysenate.seta.dao.mapper.PayPeriodRowMapper;
import gov.nysenate.seta.model.PayPeriod;
import gov.nysenate.seta.model.PayPeriodType;
import gov.nysenate.seta.model.exception.PayPeriodException;
import gov.nysenate.seta.model.exception.PayPeriodNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SqlPayPeriodDao extends SqlBaseDao implements PayPeriodDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlPayPeriodDao.class);

    protected static final String GET_PAY_PERIOD_SQL =
        "SELECT * FROM SL16PERIOD WHERE CDPERIOD = :periodType AND :date BETWEEN DTBEGIN AND DTEND";

    protected static final String GET_PAY_PERIODS_IN_RANGE_SQL =
        "SELECT * FROM SL16PERIOD\n" +
        "WHERE CDPERIOD = :periodType AND (DTBEGIN >= :startDate OR :startDate BETWEEN DTBEGIN AND DTEND)\n" +
        "                             AND (DTEND <= :endDate OR :endDate BETWEEN DTBEGIN AND DTEND)\n" +
        "ORDER BY DTBEGIN DESC";

    protected static final String GET_OPEN_ATTEND_PERIODS_SQL =
        "SELECT * FROM SL16PERIOD \n" +
        "WHERE (DTEND <= :endDate OR :endDate BETWEEN DTBEGIN AND DTEND) \n" +
        "AND CDPERIOD = 'AF' AND DTPERIODYEAR > (\n" +
        "  SELECT DISTINCT MAX(DTPERIODYEAR) OVER (PARTITION BY NUXREFEM) \n" +
        "  FROM PM23ATTEND WHERE NUXREFEM = :empId AND DTCLOSE IS NOT NULL\n" +
        ")\n" +
        "ORDER BY DTBEGIN DESC";

    /** {@inheritDoc} */
    @Override
    public PayPeriod getPayPeriod(PayPeriodType type, Date date) throws PayPeriodException {
        PayPeriod payPeriod;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("periodType", type.getCode());
        params.addValue("date", date);
        try {
            payPeriod = remoteNamedJdbc.queryForObject(GET_PAY_PERIOD_SQL, params, new PayPeriodRowMapper(""));
        }
        catch (DataRetrievalFailureException ex) {
            logger.warn("Retrieve pay period of type: {} during: {} error: {}", type, date, ex.getMessage());
            throw new PayPeriodNotFoundEx("No matching pay period(s) of type " + type + " during " + date);
        }
        return payPeriod;
    }

    /** {@inheritDoc} */
    @Override
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Date startDate, Date endDate) {
        List<PayPeriod> payPeriods;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("periodType", type.getCode());
        params.addValue("startDate", startDate);
        params.addValue("endDate", endDate);
        payPeriods = remoteNamedJdbc.query(GET_PAY_PERIODS_IN_RANGE_SQL, params, new PayPeriodRowMapper(""));
        return payPeriods;
    }

    /** {@inheritDoc} */
    @Override
    public List<PayPeriod> getOpenAttendancePayPeriods(int empId, Date endDate) {
        List<PayPeriod> payPeriods;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("endDate", endDate);
        payPeriods = remoteNamedJdbc.query(GET_OPEN_ATTEND_PERIODS_SQL, params, new PayPeriodRowMapper(""));
        return payPeriods;
    }
}

package gov.nysenate.seta.dao.base;

import gov.nysenate.seta.util.DateUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class SqlBaseDao
{
    @Resource(name = "localJdbcTemplate")
    protected JdbcTemplate localJdbc;

    @Resource(name = "localNamedJdbcTemplate")
    protected NamedParameterJdbcTemplate localNamedJdbc;

    @Resource(name = "remoteJdbcTemplate")
    protected JdbcTemplate remoteJdbc;

    @Resource(name = "remoteNamedJdbcTemplate")
    protected NamedParameterJdbcTemplate remoteNamedJdbc;

    /** The main production schema. Intended for read access only. */
    protected static String MASTER_SCHEMA = "SASS_OWNER";

    /** The time/attendance buffer schema. Permits writes. */
    protected static String TS_SCHEMA = "TS_OWNER";

    /**
     * For use in queries where we don't care about the start date.
     * @return Date
     */
    public static Date getBeginningOfTime() {
        return new DateTime(0, 1, 1, 0, 0, 0).toDate();
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDateTime.
     */
    public static LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        if (rs.getTimestamp(column) == null) return null;
        return rs.getTimestamp(column).toLocalDateTime();
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDate.
     */
    public static LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        if (rs.getDate(column) == null) return null;
        return rs.getDate(column).toLocalDate();
    }

    /**
     * Converts true to 'A' and false to 'I'
     * @param status Boolean
     * @return char
     */
    public static char getStatusCode(Boolean status) {
        return (status != null && status.equals(true)) ? 'A' : 'I';
    }
}
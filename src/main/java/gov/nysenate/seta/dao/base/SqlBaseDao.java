package gov.nysenate.seta.dao.base;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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


    /**
     * For use in queries where we don't care about the start date.
     * @return Date
     */
    public static Date getBeginningOfTime() {
        return new DateTime(0, 1, 1, 0, 0, 0).toDate();
    }

    /**
     * Convert a LocalDateTime to a Date.
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert a LocalDate to a Date.
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return toDate(localDate.atStartOfDay());
    }

    /**
     * Convert a Date to a LocalDateTime at the system's default time zone.
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDateTime.
     */
    public static LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        return getLocalDateTime(rs.getTimestamp(column));
    }

    /**
     * Convert a Date to a LocalDate at the system's default time zone.
     */
    public static LocalDate getLocalDate(Date date) {
        if (date == null) return null;
        return getLocalDateTime(date).toLocalDate();
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDate.
     */
    public static LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
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

    /**
     * Interpret 'A' as true and everything else as false.
     * @param code String
     * @return boolean
     */
    public static boolean getStatusFromCode(String code) {
        return code != null && code.equals("A");
    }

    /**
     * Replaces ${order} with either ASC or DESC.
     * @param orderByAsc boolean - if true, set as ASC, otherwise DESC.
     * @param sql String - The sql to replace
     * @return String - The new sql string
     */
    public static String setOrderByClause(boolean orderByAsc, String sql) {
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("order", (orderByAsc) ? "ASC" : "DESC");
        StrSubstitutor strSub = new StrSubstitutor(replaceMap);
        return strSub.replace(sql);
    }
}

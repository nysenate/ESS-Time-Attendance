package gov.nysenate.seta.dao.base;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
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
     * Converts true to 'A' and false to 'I'
     * @param status Boolean
     * @return char
     */
    public static char getStatusCode(Boolean status) {
        if (status != null && status.equals(true)) {
            return 'A';
        }
        else {
            return 'I';
        }
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

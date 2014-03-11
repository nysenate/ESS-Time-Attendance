package gov.nysenate.seta.dao;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.util.Date;

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

    public static Date getBeginningOfTime() {
        return new DateTime(0, 1, 1, 0, 0, 0).toDate();
    }

    public static Date getEpoch() {
        return new DateTime((long) 0).toDate();
    }
}

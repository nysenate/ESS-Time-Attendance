package gov.nysenate.seta.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;

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
}

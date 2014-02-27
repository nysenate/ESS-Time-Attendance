package gov.nysenate.seta.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Configures dependencies necessary for any database related operations.
 */
@Configuration
public class DatabaseConfig
{
    /** Local Database Configuration */
    @Value("${db.local.driver}") private String dbLocalDriver;
    @Value("${db.local.type}") private String dbLocalType;
    @Value("${db.local.host}") private String dbLocalHost;
    @Value("${db.local.name}") private String dbLocalName;
    @Value("${db.local.user}") private String dbLocalUser;
    @Value("${db.local.pass}") private String dbLocalPass;

    /** Remote Database Configuration */
    @Value("${db.remote.driver}") private String dbRemoteDriver;
    @Value("${db.remote.type}") private String dbRemoteType;
    @Value("${db.remote.host}") private String dbRemoteHost;
    @Value("${db.remote.name}") private String dbRemoteName;
    @Value("${db.remote.user}") private String dbRemoteUser;
    @Value("${db.remote.pass}") private String dbRemotePass;

    @Bean(name = "localJdbcTemplate")
    public JdbcTemplate localJdbcTemplate() {
        return new JdbcTemplate(localDataSource());
    }

    @Bean(name = "localNamedJdbcTemplate")
    public NamedParameterJdbcTemplate localNamedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(localDataSource());
    }

    @Bean(name = "remoteJdbcTemplate")
    public JdbcTemplate remoteJdbcTemplate() {
        return new JdbcTemplate(remoteDataSource());
    }

    @Bean(name = "remoteNamedJdbcTemplate")
    public NamedParameterJdbcTemplate remoteNamedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(remoteDataSource());
    }

    private DataSource localDataSource() {
        DataSource dataSource = new DataSource();
        PoolProperties pool = buildPoolProperties(dbLocalType, dbLocalHost, dbLocalName, dbLocalDriver,
                                                  dbLocalUser, dbLocalPass, 60);
        dataSource.setPoolProperties(pool);
        return dataSource;
    }

    private DataSource remoteDataSource() {
        DataSource dataSource = new DataSource();
        PoolProperties pool = buildPoolProperties(dbRemoteType, dbRemoteHost, dbRemoteName, dbRemoteDriver,
                                                  dbRemoteUser, dbRemotePass, 60);
        dataSource.setPoolProperties(pool);
        return dataSource;
    }

    /**
     * Creates a database connection pool for use in a DataSource.
     *
     * @param type Database type
     * @param host Database host address
     * @param name Database name
     * @param driver Database driver string
     * @param user Database user
     * @param pass Database password
     * @param timeoutSecs Connection timeout in seconds
     * @return PoolProperties
     */
    private PoolProperties buildPoolProperties(String type, String host, String name, String driver,
                                               String user, String pass, int timeoutSecs)
    {
        PoolProperties pool = new PoolProperties();
        final String jdbcUrlTemplate = "jdbc:%s//%s/%s";

        /** Basic connection parameters. */
        pool.setUrl(String.format(jdbcUrlTemplate, type, host, name));
        pool.setDriverClassName(driver);
        pool.setUsername(user);
        pool.setPassword(pass);

        /** How big should the connection pool be? How big can it get? */
        pool.setInitialSize(10);
        pool.setMaxActive(100);
        pool.setMinIdle(10);
        pool.setMaxIdle(100);

        pool.setDefaultAutoCommit(true);

        /** Allow for 30 seconds between validating idle connections and cleaning abandoned connections. */
        pool.setValidationInterval(30000);
        pool.setTimeBetweenEvictionRunsMillis(30000);
        pool.setMinEvictableIdleTimeMillis(30000);

        /** Configure the connection validation testing. */
        pool.setTestOnBorrow(true);
        pool.setTestOnReturn(false);
        pool.setTestWhileIdle(false);
        pool.setValidationQuery("SELECT 1");

        /**
         * Connections are considered abandoned after staying open for 60+ seconds
         * This should be set to longer than the longest expected query!
         */
        pool.setLogAbandoned(true);
        pool.setRemoveAbandoned(true);
        pool.setRemoveAbandonedTimeout(timeoutSecs);

        /** How long should we wait for a connection before throwing an exception? */
        pool.setMaxWait(10000);

        /** Allow for JMX monitoring of the connection pool */
        pool.setJmxEnabled(true);

        /** Interceptors implement hooks into the query process; like Tomcat filters.
         *  ConnectionState - Caches connection state information to avoid redundant queries.
         *  StatementFinalizer - Finalizes all related statements when a connection is closed.
         */
        pool.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        return pool;
    }
}
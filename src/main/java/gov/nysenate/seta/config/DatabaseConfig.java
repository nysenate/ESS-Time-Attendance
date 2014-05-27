package gov.nysenate.seta.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.beans.PropertyVetoException;

/**
 * Configures dependencies necessary for any database related operations.
 */
@EnableTransactionManagement
@Configuration
public class DatabaseConfig
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

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

    @Bean(name = "localTxManager")
    public PlatformTransactionManager localTxManager() {
        return new DataSourceTransactionManager(localDataSource());
    }

    @Bean(name = "remoteTxManager")
    public PlatformTransactionManager remoteTxManager() {
        return new DataSourceTransactionManager(remoteDataSource());
    }

    /**
     * Configures and returns the local data source.
     * @return ComboPooledDataSource
     */
    private ComboPooledDataSource localDataSource() {
        ComboPooledDataSource cpds = getComboPooledDataSource(dbLocalType, dbLocalHost, dbLocalName, dbLocalDriver,
                                                              dbLocalUser, dbLocalPass);
        cpds.setMinPoolSize(3);
        cpds.setMaxPoolSize(10);
        
        return cpds;
    }

    /**
     * Configures and returns the remote data source.
     * @return ComboPooledDataSource
     */
    private ComboPooledDataSource remoteDataSource() {
        ComboPooledDataSource cpds = getComboPooledDataSource(dbRemoteType, dbRemoteHost, dbRemoteName, dbRemoteDriver,
                                                              dbRemoteUser, dbRemotePass);
        cpds.setMinPoolSize(3);
        cpds.setMaxPoolSize(10);

        /** Refresh the pool every 3 hours */
        cpds.setMaxIdleTime(10800);

        /** Verify connectivity before handing over the connection. */
        cpds.setTestConnectionOnCheckout(true);
        cpds.setPreferredTestQuery("SELECT 1 FROM DUAL");

        /** Set max connection retry attempts */
        cpds.setAcquireRetryAttempts(5);

        return cpds;
    }

    /**
     * Creates a basic pooled DataSource.
     *
     * @param type Database type
     * @param host Database host address
     * @param name Database name
     * @param driver Database driver string
     * @param user Database user
     * @param pass Database password
     * @return PoolProperties
     */
    private ComboPooledDataSource getComboPooledDataSource(String type, String host, String name, String driver,
                                                           String user, String pass) {
        final String jdbcUrlTemplate = "jdbc:%s//%s/%s";
        ComboPooledDataSource pool = new ComboPooledDataSource();
        try {
            pool.setDriverClass(driver);
        }
        catch (PropertyVetoException ex) {
            logger.error("Error when setting the database driver " + driver + "{}", ex.getMessage());
        }
        pool.setJdbcUrl(String.format(jdbcUrlTemplate, type, host, name));
        pool.setUser(user);
        pool.setPassword(pass);
        return pool;
    }
}
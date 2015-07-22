package gov.nysenate.seta.config;

import gov.nysenate.seta.security.filter.EssAuthenticationFilter;
import gov.nysenate.seta.security.realm.EssAuthzRealm;
import gov.nysenate.seta.security.xsrf.XsrfTokenValidator;
import gov.nysenate.seta.security.xsrf.XsrfValidator;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.servlet.Filter;

/**
 * Configures dependencies necessary for security based functionality.
 * The security framework used is Apache Shiro (http://shiro.apache.org/).
 */
@Configuration
public class SecurityConfig
{
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${ldap.url}") private String ldapUrl;
    @Value("${ldap.dn.template}") private String ldapDnTemplate;
    @Value("${login.url:/login}") private String loginUrl;
    @Value("${login.success.url:/}") private String loginSuccessUrl;
    @Value("${xsrf.token.bytes:128}") private int xsrfBytesSize;

    @Autowired private EssAuthzRealm essAuthzRealm;

    /**
     * Shiro Filter factory that sets up the url authentication mechanism and applies the security
     * manager instance.
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setLoginUrl(loginUrl);
        shiroFilter.setSuccessUrl(loginSuccessUrl);
        shiroFilter.setFilterChainDefinitionMap(shiroIniConfig().getSection("urls"));
        return shiroFilter;
    }

    /**
     * Configures the shiro security manager with the instance of the active realm.
     */
    @Bean(name = "securityManager")
    public WebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(essAuthzRealm);
        return securityManager;
    }

    /**
     * Filter implementation used for authentication. This bean is automatically detected by the
     * ShiroFilterFactoryBean instance and can be used in the filter chain definitions by referencing
     * the bean name.
     */
    @Bean(name = "essAuthc")
    public Filter essAuthenticationFilter() {
        return new EssAuthenticationFilter();
    }

    /**
     * XsrfValidator implementation instance.
     */
    @Bean(name = "xsrfValidator")
    public XsrfValidator xsrfValidator() {
        return new XsrfTokenValidator(xsrfBytesSize);
    }

    /**
     * Exposes the shiro.ini configuration file as an Ini instance that is consumed by the
     * security filter manager when setting up the filter chains.
     */
    public Ini shiroIniConfig() {
        return Ini.fromResourcePath("classpath:shiro.ini");
    }

    /**
     * Basic realm implementation that provides hardcoded user and password for testing various roles.
     * This realm should NEVER be used in a production environment.
     */
    @Bean(name = "simpleRealm")
    public Realm senateSimpleRealm() {
        SimpleAccountRealm realm = new SimpleAccountRealm();
        realm.addRole("employee");
        realm.addRole("supervisor");
        realm.addRole("personnel");
        realm.addRole("admin");
        realm.addAccount("user", "pass", "employee", "supervisor", "personnel", "admin");
        return realm;
    }

    /**
     * Provides a configured LdapTemplate instance that can be used to perform any ldap based operations
     * against the Senate LDAP. This should typically be autowired into DAO layer classes.
     */
    @Bean(name = "ldapTemplate")
    public LdapTemplate ldapTemplate() {
        if (ldapUrl == null || ldapUrl.isEmpty()) {
            throw new BeanInitializationException("Cannot instantiate LDAP Template because ldap.url in the properties file is not set.");
        }
        logger.info("Configuring ldap template with url {}", ldapUrl);
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(ldapUrl);
        ldapContextSource.afterPropertiesSet();
        return new LdapTemplate(ldapContextSource);
    }
}

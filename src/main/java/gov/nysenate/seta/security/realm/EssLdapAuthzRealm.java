package gov.nysenate.seta.security.realm;

import gov.nysenate.seta.model.auth.LdapAuthResult;
import gov.nysenate.seta.service.auth.LdapAuthService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Realm implementation for providing authentication/authorization via the Senate's LDAP server.
 *
 * This is similar to {@link org.apache.shiro.realm.ldap.AbstractLdapRealm AbstractLdapRealm} but since
 * we're using Spring LDAP to handle low level LDAP operations it wasn't necessary to extend that class.
 */
@Component
@Profile({"dev", "test", "prod"})
public class EssLdapAuthzRealm extends AuthorizingRealm
{
    private static final Logger logger = LoggerFactory.getLogger(EssLdapAuthzRealm.class);

    @Autowired
    private LdapAuthService essLdapAuthService;

    /**
     * {@inheritDoc}
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token != null && token instanceof UsernamePasswordToken) {
            return queryForAuthenticationInfo((UsernamePasswordToken) token);
        }
        throw new UnsupportedTokenException("Senate LDAP Realm only supports UsernamePasswordToken");
    }

    /**
     * Performs an LDAP authentication using the supplied authentication token. If the authentication
     * attempt was successful an AuthenticationInfo instance will be returned that simply contains the username
     * and password supplied in the token. Otherwise an AuthenticationException will be thrown.
     * @param token AuthenticationToken
     * @return AuthenticationInfo
     * @throws AuthenticationException typically a CredentialsException is thrown if user/pass combo was invalid
     *                                 so catch that one first then fallback to AuthenticationException.
     */
    protected AuthenticationInfo queryForAuthenticationInfo(UsernamePasswordToken token) throws AuthenticationException {
        String username = token.getUsername();
        String password = new String(token.getPassword());
        logger.debug("Authenticating user {} through the Senate LDAP.", username);

        LdapAuthResult authResult = essLdapAuthService.authenticateUserByUid(username, password);
        if (authResult.isAuthenticated()) {
            return new SimpleAuthenticationInfo(authResult.getPerson(), password, getName());
        }
        switch(authResult.getAuthStatus()) {
            case EMPTY_USERNAME:
                throw new CredentialsException("The username supplied was empty.");
            case EMPTY_CREDENTIALS:
                throw new CredentialsException("The password supplied was empty.");
            case AUTHENTICATION_EXCEPTION:
                throw new CredentialsException("The username or password is invalid.");
            case MULTIPLE_MATCH_EXCEPTION:
                throw new AccountException("Multiple entries were found for the supplied username.");
            default:
                throw new AuthenticationException("An unknown exception occurred while authenticating against LDAP.");
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String user = getUsername(principals);
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
//        authInfo.addStringPermission("api:employees:view:" + users.get(user));
        return authInfo;
    }

    protected String getUsername(PrincipalCollection principals) {
        return getAvailablePrincipal(principals).toString();
    }
}

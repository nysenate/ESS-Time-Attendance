package gov.nysenate.seta.service.auth;

import gov.nysenate.seta.dao.personnel.LdapAuthDao;
import gov.nysenate.seta.model.auth.LdapAuthResult;
import gov.nysenate.seta.model.auth.LdapAuthStatus;
import gov.nysenate.seta.model.auth.SenateLdapPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.stereotype.Service;

import javax.naming.Name;

/**
 * LdapAuthService implementation that serves as the point of access for any service that requires
 * LDAP querying/authentication against the Senate LDAP server.
 */
@Service
public class EssLdapAuthService implements LdapAuthService
{
    private static final Logger logger = LoggerFactory.getLogger(EssLdapAuthService.class);

    @Autowired
    private LdapAuthDao ldapAuthDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapAuthResult authenticateUserByUid(String uid, String credentials) {
        /** Quick validation check */
        if (uid == null || uid.isEmpty()) {
            return new LdapAuthResult(LdapAuthStatus.EMPTY_USERNAME, "");
        }
        if (credentials == null || credentials.isEmpty()) {
            return new LdapAuthResult(LdapAuthStatus.EMPTY_CREDENTIALS, uid);
        }
        logger.debug("Trying to authenticate user {} with password {}", uid, credentials);
        LdapAuthStatus authStatus = LdapAuthStatus.UNKNOWN_EXCEPTION;
        try {
            Name name = ldapAuthDao.authenticateByUid(uid, credentials);
            if (name != null) {
                SenateLdapPerson person = ldapAuthDao.getPerson(name);
                return new LdapAuthResult(LdapAuthStatus.AUTHENTICATED, uid, name, person);
            }
        }
        catch(NamingException ex) {
            logger.debug("Authentication exception thrown when trying to authenticate {}", uid);
            logger.error(ex.getMessage(), ex.getExplanation());
            authStatus = LdapAuthStatus.AUTHENTICATION_EXCEPTION;
        }
        catch(IncorrectResultSizeDataAccessException ex) {
            logger.debug("Result size exception thrown when trying to authenticate {}", uid);
            authStatus = LdapAuthStatus.MULTIPLE_MATCH_EXCEPTION;
        }
        catch(Exception ex) {
            logger.warn("Unhandled exception thrown when trying to authenticate {}: {}", uid, ex.getMessage());
        }
        return new LdapAuthResult(authStatus, uid);
    }
}

package gov.nysenate.seta.service.ldap;

import gov.nysenate.seta.model.ldap.LdapAuthResult;
import gov.nysenate.seta.model.ldap.SenateLdapPerson;

import javax.naming.Name;

public interface LdapService
{
    /**
     * Authenticates an LDAP user via their uid and password. The method will always return an
     * LdapAuthResult containing a status and a resolved Name if successful.
     *
     * @param uid String username
     * @param credentials String password
     * @return LdapAuthResult
     */
    LdapAuthResult authenticateUserByUid(String uid, String credentials);
}

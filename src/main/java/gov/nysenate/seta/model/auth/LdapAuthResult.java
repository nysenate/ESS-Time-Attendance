package gov.nysenate.seta.model.auth;

import javax.naming.Name;

/**
 * This class is used to represent the outcome of an LDAP authentication request. It
 * stores the status as well as the validated Name if authentication succeeded.
 */
public class LdapAuthResult
{
    private final LdapAuthStatus authStatus;
    private final String uid;
    private final Name name;

    public LdapAuthResult(LdapAuthStatus status, String uid) {
        this(status, uid, null);
    }

    public LdapAuthResult(LdapAuthStatus status, String uid, Name name) {
        this.authStatus = status;
        this.uid = uid;
        this.name = name;
    }

    public boolean isAuthenticated() {
        return (this.authStatus != null && this.authStatus.equals(LdapAuthStatus.AUTHENTICATED));
    }

    public LdapAuthStatus getAuthStatus() {
        return authStatus;
    }

    public String getUid() {
        return uid;
    }

    public Name getName() {
        return name;
    }
}

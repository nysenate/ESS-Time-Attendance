package gov.nysenate.seta.model.ldap;

/**
 * Represents the conditions that can result after an LDAP authentication attempt.
 */
public enum LdapAuthStatus
{
    AUTHENTICATED(true, "The LDAP user has been authenticated successfully."),
    AUTHENTICATION_EXCEPTION(false, "The LDAP user could not be authenticated."),
    MULTIPLE_MATCH_EXCEPTION(false, "There were multiple matches for the given LDAP user."),
    EMPTY_USERNAME(false, "The uid was empty."),
    EMPTY_CREDENTIALS(false, "The credentials were empty."),
    UNKNOWN_EXCEPTION(false, "An exception occured while attempting to authenticate LDAP user.");

    private boolean authenticated;
    private String message;
    LdapAuthStatus(boolean authenticated, String message) {
        this.authenticated = authenticated;
        this.message = message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getMessage() {
        return message;
    }
}

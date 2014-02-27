package gov.nysenate.seta.client.response.auth;

import gov.nysenate.seta.security.AuthenticationStatus;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AuthenticationResponse
{
    protected AuthenticationStatus status;
    protected String user;
    protected String redirectUrl;

    public AuthenticationResponse(AuthenticationStatus status, String user, String redirectUrl) {
        this.status = status;
        this.user = user;
        this.redirectUrl = redirectUrl;
    }

    public boolean isAuthenticated() {
        return status.isAuthenticated();
    }

    public String getStatus() {
        return status.name();
    }

    public String getMessage() {
        return status.getStatusMessage();
    }

    public String getUser() {
        return user;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}

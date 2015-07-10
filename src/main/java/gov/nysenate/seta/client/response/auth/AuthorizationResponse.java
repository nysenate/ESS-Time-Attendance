package gov.nysenate.seta.client.response.auth;

import gov.nysenate.seta.client.view.AuthorizationStatusView;
import gov.nysenate.seta.model.auth.AuthorizationStatus;
import org.apache.shiro.subject.Subject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AuthorizationResponse
{
    @XmlElement protected AuthorizationStatusView status;
    @XmlElement protected String user;

    public AuthorizationResponse(AuthorizationStatus status, Subject subject) {
        this.status = new AuthorizationStatusView(status);
        if (subject != null && subject.getPrincipal() != null) {
            this.user = subject.getPrincipal().toString();
        }
    }

    public AuthorizationStatusView getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }
}

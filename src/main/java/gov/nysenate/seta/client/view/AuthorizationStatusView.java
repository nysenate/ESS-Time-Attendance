package gov.nysenate.seta.client.view;

import gov.nysenate.seta.model.auth.AuthorizationStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AuthorizationStatusView
{
    @XmlElement
    protected boolean authorized = false;

    @XmlElement
    protected String code;

    @XmlElement
    protected String message;

    public AuthorizationStatusView(AuthorizationStatus authorizationStatus) {
        if (authorizationStatus != null) {
            this.authorized = authorizationStatus.isAuthorized();
            this.code = authorizationStatus.name();
            this.message = authorizationStatus.getMessage();
        }
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

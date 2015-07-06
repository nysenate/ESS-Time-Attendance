package gov.nysenate.seta.client.response.base;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BaseResponse")
public abstract class BaseResponse
{
    @XmlElement public boolean success = false;
    @XmlElement public String message = "";
    @XmlElement public String responseType = "default";

    public void setMessage(String message) {
        this.message = message;
    }
}

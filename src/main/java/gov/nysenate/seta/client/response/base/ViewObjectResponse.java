package gov.nysenate.seta.client.response.base;

import gov.nysenate.seta.client.view.base.ViewObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ViewObjectResponse<ViewType extends ViewObject> extends BaseResponse
{
    @XmlElement public ViewType result;

    public ViewObjectResponse() {}

    public ViewObjectResponse(ViewType result) {
        this(result, "");
    }

    public ViewObjectResponse(ViewType result, String message) {
        this.result = result;
        if (result != null) {
            success = true;
            responseType = result.getViewType();
        }
        this.message = message;
    }
}

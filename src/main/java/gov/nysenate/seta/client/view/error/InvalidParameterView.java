package gov.nysenate.seta.client.view.error;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.client.view.base.ConstrainedParameterView;
import gov.nysenate.seta.model.exception.InvalidRequestParamEx;

public class InvalidParameterView implements ViewObject {

    protected ConstrainedParameterView parameterConstraint;
    protected String receivedValue;

    public InvalidParameterView(ConstrainedParameterView parameterConstraint, String receivedValue) {
        this.parameterConstraint = parameterConstraint;
        this.receivedValue = receivedValue;
    }

    public InvalidParameterView(String paramName, String paramType, String constraint, String receivedValue) {
        this(new ConstrainedParameterView(paramName, paramType, constraint), receivedValue);
    }

    public InvalidParameterView(InvalidRequestParamEx ex) {
        this(ex.getParameterName(), ex.getParameterType(), ex.getParameterConstraint(), ex.getParameterValue());
    }

    @Override
    public String getViewType() {
        return "invalid-parameter";
    }

    public ConstrainedParameterView getParameterConstraint() {
        return parameterConstraint;
    }

    public String getReceivedValue() {
        return receivedValue;
    }
}

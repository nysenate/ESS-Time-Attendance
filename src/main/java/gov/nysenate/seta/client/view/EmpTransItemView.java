package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.transaction.TransactionColumn;

public class EmpTransItemView implements ViewObject
{
    protected String desc;
    protected String value;

    public EmpTransItemView(String code, String value) {
        String desc = TransactionColumn.valueOf(code).getDesc();
        if (desc.isEmpty()) desc = code;
        this.desc = desc;
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getViewType() {
        return "transaction item";
    }
}

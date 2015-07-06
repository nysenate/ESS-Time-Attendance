package gov.nysenate.seta.client.response.base;

import gov.nysenate.seta.client.view.base.ListView;
import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.dao.base.LimitOffset;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class ListViewResponse<ViewType> extends PaginationResponse
{
    @XmlElement public ListView<ViewType> result;

    protected ListViewResponse(ListView<ViewType> result, int total, LimitOffset limitOffset) {
        super(total, limitOffset);
        this.result = result;
        if (result != null) {
            success = true;
            this.responseType = result.getViewType();
        }
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(List<ViewType> items, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.of(items), total, limitOffset);
    }

    public static ListViewResponse<String> ofStringList(List<String> items, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.ofStringList(items), total, limitOffset);
    }

    public static ListViewResponse<Integer> ofIntList(List<Integer> items, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.ofIntList(items), total, limitOffset);
    }
}
package gov.nysenate.seta.client.view.base;

import com.google.common.collect.ImmutableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class ListView<ViewType> implements ViewObject
{
    @XmlElement public final ImmutableList<ViewType> items;

    public static <ViewType extends ViewObject> ListView<ViewType> of(List<ViewType> items) {
        return new ListView<>(items);
    }
    public static ListView<String> ofStringList(List<String> items) {
        return new ListView<>(items);
    }
    public static ListView<Integer> ofIntList(List<Integer> items) {
        return new ListView<>(items);
    }

    private ListView(List<ViewType> items) {
        this.items = items != null ? ImmutableList.copyOf(items) : ImmutableList.of();
    }

    @XmlElement public int getSize() {
        return items.size();
    }

    @Override
    public String getViewType() {
        String listContentType = items.size()>0 ? ViewObject.getViewTypeOf(items.get(0)) : "empty";
        return listContentType + " list";
    }
}

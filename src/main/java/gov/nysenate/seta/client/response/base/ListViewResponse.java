package gov.nysenate.seta.client.response.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.nysenate.common.LimitOffset;
import gov.nysenate.seta.client.view.base.ListView;
import gov.nysenate.seta.client.view.base.ViewObject;

import javax.xml.bind.annotation.XmlElement;
import java.io.IOException;
import java.util.List;

@JsonSerialize(using = ListViewResponse.ListViewResponseJsonSerializer.class)
public class ListViewResponse<ViewType> extends PaginationResponse
{
    @XmlElement public ListView<ViewType> result;
    private String resultFieldName;

    protected ListViewResponse(ListView<ViewType> result, String resultFieldName, int total, LimitOffset limitOffset) {
        super(total, limitOffset);
        if (resultFieldName == null) {
            resultFieldName = "result";
        }
        this.resultFieldName = resultFieldName;
        this.result = result;
        if (result != null) {
            success = true;
            this.responseType = result.getViewType();
        }
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(List<ViewType> items) {
        return of(items, null,  items.size(), new LimitOffset(items.size()));
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(List<ViewType> items, String resultFieldName) {
        return of(items, resultFieldName, items.size(), new LimitOffset(items.size()));
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(
        List<ViewType> items, String resultFieldName, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.of(items), resultFieldName, total, limitOffset);
    }

    public static ListViewResponse<String> ofStringList(List<String> items, String resultFieldName, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.ofStringList(items), resultFieldName, total, limitOffset);
    }

    public static ListViewResponse<Integer> ofIntList(List<Integer> items, String resultFieldName) {
        return new ListViewResponse<>(ListView.ofIntList(items), resultFieldName, items.size(), new LimitOffset(items.size()));
    }

    public static ListViewResponse<Integer> ofIntList(List<Integer> items, String resultFieldName, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.ofIntList(items), resultFieldName, total, limitOffset);
    }

    public static class ListViewResponseJsonSerializer extends JsonSerializer<ListViewResponse>
    {
        @Override
        public void serialize(ListViewResponse listViewResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeStartObject();
            listViewResponse.serialize(listViewResponse, jsonGenerator, serializerProvider);
            jsonGenerator.writeObjectField(listViewResponse.resultFieldName, listViewResponse.result);
            jsonGenerator.writeEndObject();
        }
    }
}
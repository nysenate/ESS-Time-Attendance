package gov.nysenate.seta.client.response.base;

import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.LimitOffset;
import gov.nysenate.seta.client.view.base.ListView;
import gov.nysenate.seta.client.view.base.ViewObject;

import java.time.LocalDateTime;
import java.util.List;

public class DateRangeListViewResponse<ViewType> extends ListViewResponse<ViewType>
{
    protected LocalDateTime fromDateTime = DateUtils.LONG_AGO.atStartOfDay();
    protected LocalDateTime toDateTime = DateUtils.THE_FUTURE.atStartOfDay();

    protected DateRangeListViewResponse(ListView<ViewType> result, Range<LocalDateTime> dateTimeRange,
                                        int total, LimitOffset limitOffset) {
        super(result, total, limitOffset);
        this.fromDateTime = DateUtils.startOfDateTimeRange(dateTimeRange);
        this.toDateTime = DateUtils.endOfDateTimeRange(dateTimeRange);
    }

    public static <ViewType extends ViewObject> DateRangeListViewResponse<ViewType> of(
        List<ViewType> items, Range<LocalDateTime> dateTimeRange, int total, LimitOffset limitOffset) {
        return new DateRangeListViewResponse<>(ListView.of(items), dateTimeRange, total, limitOffset);
    }

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }
}

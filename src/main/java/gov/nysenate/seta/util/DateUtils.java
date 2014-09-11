package gov.nysenate.seta.util;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils
{
    /**
     * Given the LocalDate range, extract the lower bound LocalDate. If the lower bound is not set,
     * a really early date will be returned. If the bound is open, a single day will be added to the
     * LocalDate. If its closed, the date will remain as is.
     *
     * @param localDateRange Range<LocalDate>
     * @return LocalDate - Lower bound in the date range
     */
    public static LocalDate startOfDateRange(Range<LocalDate> localDateRange) {
        if (localDateRange != null) {
            LocalDate lower;
            if (localDateRange.hasLowerBound()) {
                lower = (localDateRange.lowerBoundType().equals(BoundType.CLOSED))
                        ? localDateRange.lowerEndpoint() : localDateRange.lowerEndpoint().plusDays(1);
            }
            else {
                lower = LocalDate.ofYearDay(1, 1);
            }
            return lower;
        }
        throw new IllegalArgumentException("Supplied localDateRange is null.");
    }

    /**
     * Given the LocalDate range, extract the upper bound LocalDate. If the upper bound is not set, a
     * date far in the future will be returned. If the bound is open, a single day will be subtracted
     * from the LocalDate. If its closed, the date will remain as is.
     *
     * @param localDateRange Range<LocalDate>
     * @return LocalDate - Upper bound in the date range
     */
    public static LocalDate endOfDateRange(Range<LocalDate> localDateRange) {
        if (localDateRange != null) {
            LocalDate upper;
            if (localDateRange.hasUpperBound()) {
                upper = (localDateRange.upperBoundType().equals(BoundType.CLOSED))
                        ? localDateRange.upperEndpoint() : localDateRange.upperEndpoint().minusDays(1);
            }
            else {
                upper = LocalDate.ofYearDay(2999, 1);
            }
            return upper;
        }
        throw new IllegalArgumentException("Supplied localDateRange is null.");
    }

    /**
     * Convert a LocalDateTime to a Date.
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert a LocalDate to a Date.
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return toDate(localDate.atStartOfDay());
    }

    /**
     * Convert a Date to a LocalDateTime at the system's default time zone.
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Convert a Date to a LocalDate at the system's default time zone.
     */
    public static LocalDate getLocalDate(Date date) {
        if (date == null) return null;
        return getLocalDateTime(date).toLocalDate();
    }
}

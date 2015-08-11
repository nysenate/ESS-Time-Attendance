package gov.nysenate.seta.service.personnel;

import com.google.common.collect.RangeSet;
import gov.nysenate.common.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface EmployeeInfoService
{
    public RangeSet<LocalDate> getEmployeeActiveDatesService(int empId);

    public default List<Integer> getEmployeeActiveYearsService(int empId) {
        RangeSet<LocalDate> rangeSet = getEmployeeActiveDatesService(empId);
        return rangeSet.asRanges().stream().flatMapToInt(r -> {
            if (r.hasLowerBound()) {
                int upperBound = (r.hasUpperBound()) ? DateUtils.endOfDateRange(r).getYear() : LocalDate.now().getYear();
                return IntStream.rangeClosed(r.lowerEndpoint().getYear(), upperBound);
            }
            return IntStream.empty();
        }).boxed().collect(Collectors.toList());
    }
}

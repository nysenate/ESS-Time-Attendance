package gov.nysenate.seta.service.personnel;

import com.google.common.collect.RangeSet;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeNotFoundEx;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface EmployeeInfoService
{
    /**
     * Retrieves an employee based on employee id. The implementation of this method should cache
     * the employees for faster retrieval than from the dao layer.
     * @param empId int
     * @return Employee
     * @exception EmployeeNotFoundEx - If employee with given empId was not found.
     */
    Employee getEmployee(int empId) throws EmployeeNotFoundEx;

    RangeSet<LocalDate> getEmployeeActiveDatesService(int empId);

    default List<Integer> getEmployeeActiveYearsService(int empId) {
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
package gov.nysenate.seta.service.personnel;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class EssEmployeeInfoService implements EmployeeInfoService
{
    private static final Logger logger = LoggerFactory.getLogger(EssEmployeeInfoService.class);

    @Autowired protected EmpTransactionService transService;

    @Override
    public RangeSet<LocalDate> getEmployeeActiveDatesService(int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        RangeSet<LocalDate> activeDates = TreeRangeSet.create();
        LocalDate currActive = null;
        for (Map.Entry<LocalDate, Boolean> status : transHistory.getEffectiveEmpStatus(DateUtils.ALL_DATES).entrySet()) {
            if (status.getValue()) {
                currActive = status.getKey();
            }
            else if (currActive != null) {
                activeDates.add(Range.closed(currActive, status.getKey()));
                currActive = null;
            }
        }
        if (currActive != null) {
            activeDates.add(Range.atLeast(currActive));
        }
        return activeDates;
    }
}

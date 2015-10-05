package gov.nysenate.seta.service.attendance.validation;

import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.client.view.error.InvalidParameterView;
import gov.nysenate.seta.model.allowances.AllowanceUsage;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.service.allowance.AllowanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Checks time records to make sure that no time record contains time entry that exceeds the employee's yearly allowance
 */
@Service
public class AllowanceTRV implements TimeRecordValidator {

    @Autowired private AllowanceService allowanceService;

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState) {
        // If the saved record contains entries where the employee was a temporary employee
        return record.getTimeEntries().stream()
                .anyMatch(entry -> entry.getPayType() == PayType.TE);
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException {
        AllowanceUsage allowanceUsage =
                allowanceService.getAllowanceUsage(record.getEmployeeId(), record.getBeginDate().getYear());

        // Get the money used for the current year, subtracting money used for the previous state of this record
        BigDecimal adjustedMoneyUsed = allowanceUsage.getMoneyUsed()
                .subtract(previousState.map(allowanceUsage::getRecordCost).orElse(BigDecimal.ZERO));

        // Get money available for this record
        BigDecimal moneyAvailable = allowanceUsage.getYearlyAllowance().subtract(adjustedMoneyUsed);

        // Get money that would be spent by the new record
        BigDecimal recordCost = allowanceUsage.getRecordCost(record);

        if (recordCost.compareTo(moneyAvailable) > 0) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.RECORD_EXCEEDS_ALLOWANCE,
                    new InvalidParameterView("recordMoneyUsed", "decimal",
                            "recordMoneyUsed <= " + moneyAvailable.toString(), recordCost.toString()));
        }
    }
}

package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.AnnualAccrualSummary;
import gov.nysenate.seta.model.accrual.PeriodAccrualSummary;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import gov.nysenate.seta.model.transaction.TransactionCode;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.*;

import static gov.nysenate.seta.model.transaction.TransactionCode.*;
import static gov.nysenate.seta.model.transaction.TransactionCode.APP;

public class SqlAccrualHelper
{
    protected static final Set<TransactionCode> PAY_TYPES = new HashSet<>(Arrays.asList(TYP, RTP, APP));
    protected static final Set<TransactionCode> MIN_TYPES = new HashSet<>(Arrays.asList(MIN, RTP, APP));

    protected static final Set<TransactionCode> APP_RTP_TYPES = new HashSet<>(Arrays.asList(RTP, APP));
    protected static final Set<TransactionCode> EMP_TYPE = new HashSet<>(Arrays.asList(EMP));
    protected static final Set<TransactionCode> APP_RTP_EMP_TYPES = new HashSet<>(Arrays.asList(EMP, RTP, APP));

    /**
     * Gets the latest annual accrual record that has a posted end date that is before our given 'payPeriodEndDate'.
     */
    static AnnualAccrualSummary getActiveAnnualSummaryFromList(Map<Integer, AnnualAccrualSummary> annualSummaries,
                                                                Date payPeriodEndDate) {
        AnnualAccrualSummary annualAccSum = null;
        if (!annualSummaries.isEmpty()) {
            Iterator<Integer> yearIterator = new TreeSet<>(annualSummaries.keySet()).descendingIterator();
            while (yearIterator.hasNext()) {
                annualAccSum = annualSummaries.get(yearIterator.next());
                if (annualAccSum.getEndDate() == null || annualAccSum.getEndDate().before(payPeriodEndDate)) {
                    break;
                }
            }
        }
        return annualAccSum;
    }

    /**
     * Get the end date of the previous pay period. Assumes pay period is contiguous.
     */
    static Date getPrevPayPeriodEndDate(PayPeriod payPeriod) {
        return new LocalDate(payPeriod.getStartDate()).minusDays(1).toDate();
    }

    /**
     * Checks if the period summary record has an end date that matches the period preceding the
     * given 'payPeriod'. This indicates it's current because since accruals are applied after a
     * period elapses and a summary record represents the state of a period after it's elapsed, a
     * pay period's available hours is essentially determined from the previous pay period's totals.
     */
    static boolean periodSummaryIsCurrent(PeriodAccrualSummary periodSummary, PayPeriod payPeriod) {
        Date prevPayPeriodEndDate = getPrevPayPeriodEndDate(payPeriod);
        return (periodSummary != null && periodSummary.getEndDate().compareTo(prevPayPeriodEndDate) == 0);
    }

    /**
     * Similar to 'periodSummaryIsCurrent' but for annual accrual summary.
     */
    static boolean annualSummaryIsCurrent(AnnualAccrualSummary annualSummary, PayPeriod payPeriod) {
        //logger.debug(OutputUtils.toJson(annualSummary));
        Date prevPayPeriodEndDate = getPrevPayPeriodEndDate(payPeriod);
        return (annualSummary != null && annualSummary.getEndDate() != null &&
                annualSummary.getEndDate().compareTo(prevPayPeriodEndDate) == 0);
    }

    /**
     * Returns true if the first item in the list of trans records indicates that the employee was terminated.
     */
    static boolean isEmployeeTerminated(LinkedList<TransactionRecord> empRecords) {
        for (TransactionRecord rec : empRecords) {
            if (APP_RTP_TYPES.contains(rec.getTransType())) {
                return false;
            }
            else if (EMP_TYPE.contains(rec.getTransType())) {
                return true;
            }
        }
        return false;
    }

    static boolean isEmployeeAppointed(LinkedList<TransactionRecord> empRecords) {
        for (TransactionRecord rec : empRecords) {
            if (APP_RTP_TYPES.contains(rec.getTransType())) {
                return true;
            }
            else if (EMP_TYPE.contains(rec.getTransType())) {
                return false;
            }
        }
        return false;
    }

    /**
     * Looks up the first non null min pay type value in the given list of trans recs.
     * Returns null if nothing found.
     */
    static PayType getPayType(LinkedList<TransactionRecord> payTypeRecs) throws AccrualException {
        for (TransactionRecord rec : payTypeRecs) {
            if (PAY_TYPES.contains(rec.getTransType()) && rec.hasNonNullValue("CDPAYTYPE")) {
                return PayType.valueOf(rec.getValue("CDPAYTYPE"));
            }
        }
        return null;
    }

    /**
     * Looks up the first non null min hours value in the given list of trans recs.
     * Returns null if nothing found.
     */
    static BigDecimal getMinTotalHours(LinkedList<TransactionRecord> minHourRecs) {
        for (TransactionRecord rec : minHourRecs) {
            if (MIN_TYPES.contains(rec.getTransType()) && rec.getValue("NUMINTOTHRS") != null) {
                return new BigDecimal(rec.getValue("NUMINTOTHRS"));
            }
        }
        return null;
    }

    /**
     * Looks up the first non null 'min hours until end of year' value in the given list of trans recs.
     * Retursn null if nothing found.
     */
    static BigDecimal getMinEndHours(LinkedList<TransactionRecord> minHourRecs) throws AccrualException {
        for (TransactionRecord rec : minHourRecs) {
            if (MIN_TYPES.contains(rec.getTransType()) && rec.getValue("NUMINTOTEND") != null) {
                return new BigDecimal(rec.getValue("NUMINTOTEND"));
            }
        }
        return null;
    }

    /**
     * Returns a list of just min type transactions from the given history
     */
    static LinkedList<TransactionRecord> getMinRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionCode> types = new LinkedHashSet<>(Arrays.asList(MIN, RTP, APP));
        return transHistory.getTransRecords(types, orderByAsc);
    }

    /**
     * Returns a list of just employee status transactions from the given history
     */
    static LinkedList<TransactionRecord> getEmpRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionCode> types = new LinkedHashSet<>(Arrays.asList(RTP, EMP, APP));
        return transHistory.getTransRecords(types, orderByAsc);
    }

    /**
     * Returns a list of just pay type transactions from the given history
     */
    static LinkedList<TransactionRecord> getPayTypeRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionCode> types = new LinkedHashSet<>(Arrays.asList(TYP, RTP, APP));
        return transHistory.getTransRecords(types, orderByAsc);
    }
}

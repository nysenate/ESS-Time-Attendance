package gov.nysenate.seta.dao.accrual;

import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.AnnualAccSummary;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.seta.model.exception.TransactionHistoryException;
import gov.nysenate.seta.model.exception.TransactionHistoryNotFoundEx;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.transaction.AuditHistory;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.seta.model.transaction.TransactionCode.*;

public class SqlAccrualHelper
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualHelper.class);

    protected static final Set<TransactionCode> PAY_CODES = new HashSet<>(Arrays.asList(TYP, RTP, APP));
    protected static final Set<TransactionCode> MIN_CODES = new HashSet<>(Arrays.asList(MIN, RTP, APP));

    protected static final Set<TransactionCode> APP_RTP_CODES = new HashSet<>(Arrays.asList(RTP, APP));
    protected static final Set<TransactionCode> EMP_CODE = new HashSet<>(Arrays.asList(EMP));
    protected static final Set<TransactionCode> APP_RTP_EMP_CODES = new HashSet<>(Arrays.asList(EMP, RTP, APP));

    /**
     * Gets the latest annual accrual record that has a posted end date that is before our given 'payPeriodEndDate'.
     */
    static AnnualAccSummary getActiveAnnualSummaryFromList(Map<Integer, AnnualAccSummary> annualSummaries,
                                                                LocalDate payPeriodEndDate) {
        AnnualAccSummary annualAccSum = null;
        if (!annualSummaries.isEmpty()) {
            Iterator<Integer> yearIterator = new TreeSet<>(annualSummaries.keySet()).descendingIterator();
            while (yearIterator.hasNext()) {
                annualAccSum = annualSummaries.get(yearIterator.next());
                if (annualAccSum.getEndDate() == null || annualAccSum.getEndDate().isBefore(payPeriodEndDate)) {
                    break;
                }
            }
        }
        return annualAccSum;
    }

    /**
     * Get the end date of the previous pay period. Assumes pay period is contiguous.
     */
    static java.time.LocalDate getPrevPayPeriodEndDate(PayPeriod payPeriod) {
        return payPeriod.getStartDate().minusDays(1);
    }

    /**
     * Checks if the period summary record has an end date that matches the period preceding the
     * given 'payPeriod'. This indicates it's current because since accruals are applied after a
     * period elapses and a summary record represents the state of a period after it's elapsed, a
     * pay period's available hours is essentially determined from the previous pay period's totals.
     */
    static boolean periodSummaryIsCurrent(PeriodAccSummary periodSummary, PayPeriod payPeriod) {
        java.time.LocalDate prevPayPeriodEndDate = getPrevPayPeriodEndDate(payPeriod);
        return (periodSummary != null && periodSummary.getEndDate().compareTo(prevPayPeriodEndDate) == 0);
    }

    /**
     * Similar to 'periodSummaryIsCurrent' but for annual accrual summary.
     */
    static boolean annualSummaryIsCurrent(AnnualAccSummary annualSummary, PayPeriod payPeriod) {
        //logger.debug(OutputUtils.toJson(annualSummary));
        java.time.LocalDate prevPayPeriodEndDate = getPrevPayPeriodEndDate(payPeriod);
        return (annualSummary != null && annualSummary.getEndDate() != null &&
                annualSummary.getEndDate().compareTo(prevPayPeriodEndDate) == 0);
    }

    /**
     * Returns true if the first item in the list of trans records indicates that the employee was terminated.
     */
    static boolean isEmployeeTerminated(LinkedList<TransactionRecord> empRecords) {
        for (TransactionRecord rec : empRecords) {
            if (APP_RTP_CODES.contains(rec.getTransCode())) {
                return false;
            }
            else if (EMP_CODE.contains(rec.getTransCode())) {
                return true;
            }
        }
        return false;
    }

    static boolean isEmployeeAppointed(LinkedList<TransactionRecord> empRecords) {
        for (TransactionRecord rec : empRecords) {
            if (APP_RTP_CODES.contains(rec.getTransCode())) {
                return true;
            }
            else if (EMP_CODE.contains(rec.getTransCode())) {
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
            if (PAY_CODES.contains(rec.getTransCode()) && rec.hasNonNullValue("CDPAYTYPE")) {
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
            if (MIN_CODES.contains(rec.getTransCode()) && rec.getValue("NUMINTOTHRS") != null) {
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
            if (MIN_CODES.contains(rec.getTransCode()) && rec.getValue("NUMINTOTEND") != null) {
                return new BigDecimal(rec.getValue("NUMINTOTEND"));
            }
        }
        return null;
    }

    /**
     * Returns a list of just min type transactions from the given history
     */
    // FIX ME PLX
    static LinkedList<TransactionRecord> getMinRecordsFromHistory(TransactionHistory transHistory, SortOrder dateOrder) {
//        Set<TransactionCode> codes = new LinkedHashSet<>(Arrays.asList(MIN, RTP, APP));
//        return transHistory.getTransRecords(codes, orderByAsc);
        throw new NotImplementedException();
    }

    /**
     * Returns a list of just employee status transactions from the given history
     */
    static LinkedList<TransactionRecord> getEmpRecordsFromHistory(TransactionHistory transHistory, SortOrder dateOrder) {
//        Set<TransactionCode> codes = new LinkedHashSet<>(Arrays.asList(RTP, EMP, APP));
//        return transHistory.getTransRecords(codes, orderByAsc);
        throw new NotImplementedException();
    }

    /**
     * Returns a list of just pay type transactions from the given history
     */
    static LinkedList<TransactionRecord> getPayTypeRecordsFromHistory(TransactionHistory transHistory, SortOrder dateOrder) {
//        Set<TransactionCode> codes = new LinkedHashSet<>(Arrays.asList(TYP, RTP, APP));
//        return transHistory.getTransRecords(codes, orderByAsc);
        throw new NotImplementedException();
    }

    /**
     * Returns the expected employee hours given a period of time.
     * FIX ME PLOX
     */
    public static int getExpectedHours(TransactionHistory transHistory, Date dtstart, Date dtend) {
        int expectedHours = 0;
        AuditHistory auditHistory = new AuditHistory();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String sdtstart = null;
        try {
            sdtstart =simpleDateFormat.format(dtstart);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String sdtend = null;
        try {
            sdtend =simpleDateFormat.format(dtend);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            logger.debug("==================================================================================================================================");
            logger.debug("getExpectedHours Emp Id:"+transHistory.getEmployeeId()+" date range:"+sdtstart+" - "+sdtend);
            logger.debug("==================================================================================================================================");
            auditHistory.setTransactionHistory(transHistory);
            List<Map> auditRecords = auditHistory.getAuditRecords();
            Date dteffect = null;
            Date dteffectEnd = null;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
            Map currentRec = null;
            Map nextRec = null;
            for (int x=0;x<auditRecords.size();x++) {
                currentRec = auditRecords.get(x);
                if (x<auditRecords.size()-1) {
                    nextRec  = auditRecords.get(x+1);
                }
                else {
                    nextRec = null;
                }
                try {
                    dteffect = sdf.parse((String) currentRec.get("EffectDate"));
                    /*
                    * TODO: For certain cdstatper changes, set dteffectEnd to nextRec.dteffect and dteffect = currentRec.dteffect+1
                    * In some rare cases of cdstatper, change occurs in effect date +1
                    * instead of effect date.
                     */
                    if (nextRec == null) {
                        LocalDate localDate = LocalDate.now();
                        localDate.plusMonths(1);
                        dteffectEnd = DateUtils.toDate(localDate);
                        logger.debug("- getExpectedHours Emp Id:" + transHistory.getEmployeeId() + " Last Record Effective (1 Month future:" + sdf.format(dteffectEnd) + ")");
                    } else {
                        dteffectEnd = sdf.parse((String) nextRec.get("EffectDate"));
                        LocalDate localDate = DateUtils.getLocalDate(dteffectEnd);
                        localDate = localDate.plusDays(-1);
                        dteffectEnd = DateUtils.toDate(localDate);
                        logger.debug("- getExpectedHours Emp Id:" + transHistory.getEmployeeId() + " Normal Record Effective:" + sdf.format(dteffectEnd));
                    }

                    /*
                    * Make sure that we do not include hours outside of the Start Date
                    * and End Date parameters.
                     */
                    if (dtstart.after(dteffect) && !dtstart.after(dteffectEnd)) {
                        dteffect = dtstart;
                    }
                    if (dtend.before(dteffectEnd) && !dtend.before(dteffect)) {
                        dteffectEnd = dtend;
                    }
                    String currentPaytype = (String) currentRec.get("CDPAYTYPE");
                    String currentEmpStatus = (String) currentRec.get("CDEMPSTATUS");
                    String currentAgencyCode = (String) currentRec.get("CDAGENCY");
                    String currentStatusPer = (String) currentRec.get("CDSTATPER");

                    /*
                    * Employee has to be an Active Non-Senator employee who is not on
                    * Leave Without Pay when adding expected hours.
                     */
                   logger.debug("-----------currentEmpStatus=="+currentEmpStatus+", currentAgencyCode=="+currentAgencyCode+", currentStatusPer=="+currentStatusPer);

                    /*
                    * Agency Code commented for testing since there is an issue with Null
                    * Agency Codes when a value should exist.
                     */

                    if (currentEmpStatus!=null && currentEmpStatus.equalsIgnoreCase("A") && currentAgencyCode!=null /*&& !currentAgencyCode.equals("04210")*/ && currentStatusPer!=null && !currentStatusPer.equalsIgnoreCase("LWOP"))
                    {
                        logger.debug("---------------PAYTYPE:("+currentPaytype+")");
                        if (!dteffect.before(dtstart) && !dteffectEnd.after(dtend)) {
                            if (currentPaytype.equalsIgnoreCase("RA")) {
                                logger.debug("---------------PAYTYPE:(RA):" + expectedHours + " + " + PeriodAccSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd) + " * 7");
                                logger.debug("---------------PAYTYPE:(RA):PeriodAccSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd):" + PeriodAccSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd));

                                expectedHours = expectedHours + PeriodAccSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd) * 7;
                            } else if (currentPaytype.equalsIgnoreCase("SA")) {
                                int proRate = 7;
                                logger.debug("---------------PAYTYPE:(SA):" + expectedHours + " + " + PeriodAccSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd) + " * " + proRate);
                                // TODO  need to calculate proRate;
                                expectedHours = expectedHours + PeriodAccSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd) * proRate;
                            } else if (currentPaytype.equalsIgnoreCase("TE")) {
                                logger.debug("---------------PAYTYPE:(TE) NEEDS CODING");
                                // TODO  need to sum up TE Hours worked;
                            }
                        }
                        String sdteffect = null;
                        try {
                            sdteffect = simpleDateFormat.format(dteffect);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        /*
                        * The code assumes that the Transaction History is in Effect Date Ascending Order
                         */

                        String sdteffectEnd = null;
                        try {
                            sdteffectEnd = simpleDateFormat.format(dteffectEnd);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        logger.debug("getExpectedHours Emp Id:"+transHistory.getEmployeeId()+" Effective:"+sdteffect+" - "+sdteffectEnd+", PAYTYPE:"+currentPaytype+", Exp Hours Subtotal:"+expectedHours);
                    }

                }
                catch (ParseException e) {
                     e.printStackTrace();
                }
            }

        } catch (TransactionHistoryNotFoundEx transactionHistoryNotFoundEx) {
            transactionHistoryNotFoundEx.printStackTrace();
        } catch (TransactionHistoryException e) {
            e.printStackTrace();
        }

        return expectedHours;

    }
}

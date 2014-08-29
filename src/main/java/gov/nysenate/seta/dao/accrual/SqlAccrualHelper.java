package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.dao.allowances.SqlTEHoursDao;
import gov.nysenate.seta.dao.allowances.TEHoursDao;
import gov.nysenate.seta.model.accrual.AccrualException;
import gov.nysenate.seta.model.accrual.AnnualAccrualSummary;
import gov.nysenate.seta.model.accrual.PeriodAccrualSummary;
import gov.nysenate.seta.model.allowances.TEHours;
import gov.nysenate.seta.model.exception.TransactionHistoryException;
import gov.nysenate.seta.model.exception.TransactionHistoryNotFoundEx;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.transaction.AuditHistory;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.model.transaction.TransactionHistory;
import gov.nysenate.seta.model.transaction.TransactionRecord;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Autowired
    private SqlTEHoursDao tEHoursDao;

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
    static LinkedList<TransactionRecord> getMinRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionCode> codes = new LinkedHashSet<>(Arrays.asList(MIN, RTP, APP));
        return transHistory.getTransRecords(codes, orderByAsc);
    }

    /**
     * Returns a list of just employee status transactions from the given history
     */
    static LinkedList<TransactionRecord> getEmpRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionCode> codes = new LinkedHashSet<>(Arrays.asList(RTP, EMP, APP));
        return transHistory.getTransRecords(codes, orderByAsc);
    }

    /**
     * Returns a list of just pay type transactions from the given history
     */
    static LinkedList<TransactionRecord> getPayTypeRecordsFromHistory(TransactionHistory transHistory, boolean orderByAsc) {
        Set<TransactionCode> codes = new LinkedHashSet<>(Arrays.asList(TYP, RTP, APP));
        return transHistory.getTransRecords(codes, orderByAsc);
    }

    public  BigDecimal getActualHours(int nuxrefem, Date dtstart, Date dtend) {
       BigDecimal  actualHours = new BigDecimal("0");

       return actualHours;
    }

    /**
     * Returns the expected employee hours given a period of time
     */
    public  BigDecimal getExpectedHours(TransactionHistory transHistory, Date dtstart, Date dtend) {
        BigDecimal  expectedHours = new BigDecimal("0");
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

                   /**
                    * TODO: For certain cdstatper changes, set dteffectEnd to nextRec.dteffect and dteffect = currentRec.dteffect+1
                    * In some rare cases of cdstatper, change occurs in effect date +1
                    * instead of effect date.
                    */

                    if (nextRec == null) {
                        LocalDate localDate = new LocalDate(new Date());
                        localDate.plusMonths(1);
                        dteffectEnd = localDate.toDate();
                        logger.debug("- getExpectedHours Emp Id:" + transHistory.getEmployeeId() + " Last Record Effective (1 Month future:" + sdf.format(dteffectEnd) + ")");
                    } else {
                        dteffectEnd = getNextEffectDate(currentRec, nextRec);
                        LocalDate localDate = new LocalDate(dteffectEnd);
                        localDate = localDate.plusDays(-1);
                        dteffectEnd = localDate.toDate();
                        logger.debug("- getExpectedHours Emp Id:" + transHistory.getEmployeeId() + " Normal Record Effective:" + sdf.format(dteffectEnd));
                    }

                   /**
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

                   /**
                    * Employee has to be an Active Non-Senator employee who is not on
                    * Leave Without Pay when adding expected hours.
                    */

                   logger.debug("-----------currentEmpStatus=="+currentEmpStatus+", currentAgencyCode=="+currentAgencyCode+", currentStatusPer=="+currentStatusPer);

                   /**
                    * Agency Code commented for testing since there is an issue with Null
                    * Agency Codes when a value should exist.
                    */

                    if (currentEmpStatus!=null && currentEmpStatus.equalsIgnoreCase("A") && currentAgencyCode!=null /*&& !currentAgencyCode.equals("04210")*/ && currentStatusPer!=null && !currentStatusPer.equalsIgnoreCase("LWOP"))
                    {
                        logger.debug("---------------PAYTYPE:("+currentPaytype+")");
                        if (!dteffect.before(dtstart) && !dteffectEnd.after(dtend)) {
                            if (currentPaytype.equalsIgnoreCase("RA")) {
                                logger.debug("---------------PAYTYPE:(RA):" + expectedHours + " + " + PeriodAccrualSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd) + " * 7");
                                logger.debug("---------------PAYTYPE:(RA):PeriodAccrualSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd):" + PeriodAccrualSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd));
                                expectedHours = expectedHours.add(new BigDecimal(PeriodAccrualSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd) * 7.0), MathContext.DECIMAL64);
                            } else if (currentPaytype.equalsIgnoreCase("SA")) {

                                /*
                                    double is loosing the precision of BigDecimal but using it for now since it is being rounded
                                    to the nearest quater hour, we may not need the precision of Big Decimal
                                 */

                                logger.debug("SA Prorated: 7.0 * "+SqlAccrualHelper.saProrate(currentRec).doubleValue()+" * 4.0)/4.0");
                                double proRate = Math.round(7.0 * SqlAccrualHelper.saProrate(currentRec).doubleValue()  * 4.0)/4.0;
                                logger.debug("---------------PAYTYPE:(SA):" + expectedHours + " + " + PeriodAccrualSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd) + " * " + proRate);

                                logger.debug("---------------ADDING HOURS:" + new BigDecimal(((double)PeriodAccrualSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd)) * proRate));

                                expectedHours = expectedHours.add(new BigDecimal(((double) PeriodAccrualSummary.getWorkingDaysBetweenDates(dteffect, dteffectEnd)) * proRate), MathContext.DECIMAL64);
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

                       /**
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
//        teHourses = tEHoursDao.getTEHours(transHistory.getEmployeeId(), dtstart, dtend);
 //       expectedHours.add(new BigDecimal(String.valueOf(tEHoursDao.sumTEHours(teHourses).getTEHours())));
        ArrayList<TEHours> teHourses;
        //TEHoursDao tEHoursDao = new SqlTEHoursDao();
        if (transHistory==null) {
            logger.debug("********transHistory IS NULL");
        }
        else {
            logger.debug("********transHistory xref:"+transHistory.getEmployeeId());
        }

        if (dtstart==null) {
            logger.debug("********dtstart IS NULL");
        }

        if (dtend==null) {
            logger.debug("********dtend IS NULL");
        }

        if (tEHoursDao==null) {
            logger.debug("********tEHoursDao IS NULL");
            tEHoursDao = new SqlTEHoursDao();
        }

        teHourses = tEHoursDao.getTEHours(transHistory.getEmployeeId(), dtstart, dtend);
        expectedHours.add(new BigDecimal(String.valueOf(tEHoursDao.sumTEHours(teHourses).getTEHours())));

        return expectedHours;

    };

    public void testTeHours() {
        logger.debug("Before getting records");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        ArrayList<TEHours> teHourses = tEHoursDao.getTEHours(11225, 2014);
        for (TEHours curTEHours : teHourses) {
            logger.debug("Current TE Hours:"+sdf.format(curTEHours.getBeginDate())+" - "+sdf.format(curTEHours.getEndDate())+": "+curTEHours.getTEHours());
        }
        logger.debug("Record Count:"+teHourses.size());
        logger.debug("total:"+tEHoursDao.sumTEHours(teHourses).getTEHours());
        //return tEHoursDao.sumTEHours(teHourses).getTEHours()
    }

   /**
    * Get the Next Record's Effect date, current record has been included in order to
    * check to see if the Personnel Status Code has changed from the current record to
    * the next record, if it has and the next record
    */

    private static Date getNextEffectDate(Map currentRec, Map nextRec) {
        Date returnNextEffectDate = null;
        Date nextEffectDate = null;
        String currentStatPer = null;
        String nextStatPer = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        if (nextRec!=null) {
            currentStatPer = (String) currentRec.get("CDSTATPER");
            nextStatPer = (String) nextRec.get("CDSTATPER");

            try {
                nextEffectDate = sdf.parse((String) nextRec.get("EffectDate"));
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

            if (currentStatPer==null && nextStatPer != null) {
                return nextEffectDate;
            }
            else if (currentStatPer == null && nextStatPer == null) {
                return null;
            }
            else {
                if (currentStatPer.equalsIgnoreCase(nextStatPer)) {
                    return nextEffectDate;
                }
                else {
                    if (nextStatPer.equalsIgnoreCase("RSGN")) {
                        LocalDate localDate = new LocalDate(nextEffectDate);
                        localDate = localDate.plusDays(1);
                        returnNextEffectDate = localDate.toDate();
                    }
                }
            }
        }

        return returnNextEffectDate;
    }

 /**
  * Prorate based on calculated days (7 Hours per day) along with Number of Min Hours to Year
  * End. Return the calculated prorate as a decimal value between 0 and 1.
  */

    public static BigDecimal saProrate (Map currentRec) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfmmddyy = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

        Date appointFromDate = null;
        Date effectDate = null;
        Date janDate = null;
        Date decDate = null;
        Date startDate = null;
        Date endDate = null;
        String year = null;
        LocalDate localDate;
        int expectedDays = 0;
        int workDays = 0;
        int workHours = 0;
        int numberOfHoursToYearEnd = 0;
        BigDecimal proRate = null;

        try {
            effectDate = sdfmmddyy.parse((String) currentRec.get("EffectDate"));
            year = sdfYear.format(effectDate);
            try {
                localDate = new LocalDate(Integer.parseInt(year), 1, 1);
                janDate = localDate.toDate();
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            try {
                localDate = new LocalDate(Integer.parseInt(year), 12, 31);
                decDate = localDate.toDate();

            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            appointFromDate = sdf.parse(((String) currentRec.get("DTAPPOINTFRM")).substring(0, 10));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date appointToDate = null;
        try {
            appointToDate = sdf.parse(((String) currentRec.get("DTAPPOINTTO")).substring(0, 10));
        } catch (ParseException e) {
            e.printStackTrace();
    }

        if (appointFromDate.before(janDate)) {
            startDate = janDate;
        }
        else {
            startDate = appointFromDate;
        }

        if (appointToDate.after(decDate)) {
            endDate = decDate;
        }
        else {
            endDate = appointToDate;
        }

        workDays = PeriodAccrualSummary.getWorkingDaysBetweenDates(startDate, endDate);

        workHours = workDays  * 7;

         try {
             numberOfHoursToYearEnd = new Integer((String) currentRec.get("NUMINTOTEND")).intValue();
         }
         catch (Exception e) {
                e.printStackTrace();
         }
        logger.debug("PRORATE:" + numberOfHoursToYearEnd + "/" + workHours);
        proRate = new BigDecimal(((double)numberOfHoursToYearEnd / (double)workHours));

        return proRate;
    }
}

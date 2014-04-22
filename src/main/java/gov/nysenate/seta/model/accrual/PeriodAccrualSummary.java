package gov.nysenate.seta.model.accrual;

import gov.nysenate.seta.model.period.PayPeriod;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Helper class to store accrual summary information per pay period.
 * Also contains the accrual rates and expected hours, which are not
 * included in the AnnualAccrualSummary.
 */
public class PeriodAccrualSummary extends AccrualSummary
{
    int year;

    /** Indicates the pay period for which we want the current state of accruals for. */
    PayPeriod payPeriod;

    /** The base pay period is a previous pay period that contains the summary data for
     *  which these accruals are based off of. Basically the accrual information for a given pay
     *  period should reflect the hours available at the start of that pay period. Since hours are
     *  accrued upon the completion of a pay period we need the accrued/usage totals of the
     *  preceding pay period. Often we won't have the totals for the preceding period and will use
     *  an earlier period and calculate the hours in between.
     */
    PayPeriod basePayPeriod;

    BigDecimal prevTotalHours;
    BigDecimal expectedTotalHours;
    BigDecimal expectedBiweekHours;
    BigDecimal sickRate;
    BigDecimal vacRate;

    public PeriodAccrualSummary() {}

    public PeriodAccrualSummary(AccrualSummary summary) {
        super(summary);
    }

    /** --- Functional Getters/Setters --- */

    public Date getEndDate() {
        if (basePayPeriod != null) {
            return basePayPeriod.getEndDate();
        }
        throw new IllegalStateException("Base pay period was not set in period accrual summary.");
    }

    /** --- Basic Getters/Setters --- */

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
    }

    public PayPeriod getBasePayPeriod() {
        return basePayPeriod;
    }

    public void setBasePayPeriod(PayPeriod basePayPeriod) {
        this.basePayPeriod = basePayPeriod;
    }

    public BigDecimal getPrevTotalHours() {
        return prevTotalHours;
    }

    public void setPrevTotalHours(BigDecimal prevTotalHours) {
        this.prevTotalHours = prevTotalHours;
    }

    public BigDecimal getExpectedTotalHours() {
        return expectedTotalHours;
    }

    public void setExpectedTotalHours(BigDecimal expectedTotalHours) {
        this.expectedTotalHours = expectedTotalHours;
    }

    public BigDecimal getExpectedBiweekHours() {
        return expectedBiweekHours;
    }

    public void setExpectedBiweekHours(BigDecimal expectedBiweekHours) {
        this.expectedBiweekHours = expectedBiweekHours;
    }

    public BigDecimal getSickRate() {
        return sickRate;
    }

    public void setSickRate(BigDecimal sickRate) {
        this.sickRate = sickRate;
    }

    public BigDecimal getVacRate() {
        return vacRate;
    }

    public void setVacRate(BigDecimal vacRate) {
        this.vacRate = vacRate;
    }
}
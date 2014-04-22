package gov.nysenate.seta.model.accrual;

import gov.nysenate.seta.model.payroll.PayType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;

/**
 * This class was intended for use within the accrual dao layer. It contains the necessary information
 * needed to compute accruals for a given pay period and provides methods to transfer data to/from other
 * accrual related classes.
 */
public class AccrualState extends AccrualSummary
{
    protected Date endDate;
    protected boolean employeeActive;
    protected PayType payType;
    protected BigDecimal minTotalHours;
    protected BigDecimal minHoursToEnd;
    protected BigDecimal sickRate;
    protected BigDecimal vacRate;
    int periodCounter = 0;

    public AccrualState(AccrualSummary summary) {
        super(summary);
    }

    /** --- Functional Getters/Setters --- */

    private static MathContext FOUR_DIGITS_MAX = new MathContext(4);

    public BigDecimal getProratePercentage() {
        if (this.minTotalHours != null) {
            return this.minTotalHours.divide(new BigDecimal(1820), FOUR_DIGITS_MAX);
        }
        return BigDecimal.ZERO;
    }

    public void incrementAccrualsEarned() {
        this.setVacHoursAccrued(this.getVacHoursAccrued().add(this.vacRate));
        this.setEmpHoursAccrued(this.getEmpHoursAccrued().add(this.sickRate));
    }

    public void applyYearRollover() {
        resetUsage();
        this.setVacHoursBanked(this.getVacHoursBanked().add(this.getVacHoursAccrued()));
        this.setVacHoursAccrued(BigDecimal.ZERO);
        this.setEmpHoursBanked(this.getEmpHoursBanked().add(this.getEmpHoursAccrued()));
        this.setEmpHoursAccrued(BigDecimal.ZERO);
    }

    public void resetUsage() {
        this.setVacHoursUsed(BigDecimal.ZERO);
        this.setPerHoursUsed(BigDecimal.ZERO);
        this.setEmpHoursUsed(BigDecimal.ZERO);
        this.setHolHoursUsed(BigDecimal.ZERO);
        this.setMiscHoursUsed(BigDecimal.ZERO);
        this.setWorkHours(BigDecimal.ZERO);
        this.setTravelHoursUsed(BigDecimal.ZERO);
    }

    public void applyUsage(PeriodAccrualUsage usage) {
        this.setVacHoursUsed(this.getVacHoursUsed().add(usage.getVacHoursUsed()));
        this.setPerHoursUsed(this.getPerHoursUsed().add(usage.getPerHoursUsed()));
        this.setEmpHoursUsed(this.getEmpHoursUsed().add(usage.getEmpHoursUsed()));
        this.setHolHoursUsed(this.getHolHoursUsed().add(usage.getHolHoursUsed()));
        this.setMiscHoursUsed(this.getMiscHoursUsed().add(usage.getMiscHoursUsed()));
        this.setWorkHours(this.getWorkHours().add(usage.getWorkHours()));
        this.setTravelHoursUsed(this.getTravelHoursUsed().add(usage.getTravelHoursUsed()));
    }

    /** --- Basic Getters/Setters --- */

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isEmployeeActive() {
        return employeeActive;
    }

    public void setEmployeeActive(boolean isTerminated) {
        this.employeeActive = isTerminated;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public BigDecimal getMinTotalHours() {
        return minTotalHours;
    }

    public void setMinTotalHours(BigDecimal minTotalHours) {
        this.minTotalHours = minTotalHours;
    }

    public BigDecimal getMinHoursToEnd() {
        return minHoursToEnd;
    }

    public void setMinHoursToEnd(BigDecimal minHoursToEnd) {
        this.minHoursToEnd = minHoursToEnd;
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

    public int getPeriodCounter() {
        return periodCounter;
    }

    public void setPeriodCounter(int periodCounter) {
        this.periodCounter = periodCounter;
    }
}

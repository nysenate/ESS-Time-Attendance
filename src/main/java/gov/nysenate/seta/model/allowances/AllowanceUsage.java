package gov.nysenate.seta.model.allowances;

import java.math.BigDecimal;

public class AllowanceUsage {

    protected int empId;
    protected int year;

    /** The amount of hours worked and money paid that has been officially paid for (according to transactions) */
    protected BigDecimal baseHoursUsed;
    protected BigDecimal baseMoneyUsed;

    /** The amount of hours worked/money used as recorded in time records for periods not covered in transaction history */
    protected BigDecimal recordHoursUsed;
    protected BigDecimal recordMoneyUsed;

    public AllowanceUsage(int empId, int year,
                          BigDecimal baseHoursUsed, BigDecimal baseMoneyUsed,
                          BigDecimal recordHoursUsed, BigDecimal recordMoneyUsed) {
        this.empId = empId;
        this.year = year;
        this.baseHoursUsed = baseHoursUsed;
        this.baseMoneyUsed = baseMoneyUsed;
        this.recordHoursUsed = recordHoursUsed;
        this.recordMoneyUsed = recordMoneyUsed;
    }

    /** --- Functional Getters / Setters --- */

    public BigDecimal getHoursUsed() {
        return baseHoursUsed.add(recordHoursUsed);
    }

    public BigDecimal getMoneyUsed() {
        return baseMoneyUsed.add(recordMoneyUsed);
    }

    /** --- Getters / Setters --- */

    public int getEmpId() {
        return empId;
    }

    public int getYear() {
        return year;
    }

    public BigDecimal getBaseHoursUsed() {
        return baseHoursUsed;
    }

    public BigDecimal getBaseMoneyUsed() {
        return baseMoneyUsed;
    }

    public BigDecimal getRecordHoursUsed() {
        return recordHoursUsed;
    }

    public BigDecimal getRecordMoneyUsed() {
        return recordMoneyUsed;
    }
}

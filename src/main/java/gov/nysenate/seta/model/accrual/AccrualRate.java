package gov.nysenate.seta.model.accrual;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides accrual rates for vacation and sick time based on number of biweekly pay periods.
 * These rates apply only to regular annual employees.
 */
public enum AccrualRate
{
    /** Vacation rates increase as you work longer until you reach 5.5 */
    VACATION (Arrays.asList(new BigDecimal(0), new BigDecimal("31.5"), new BigDecimal("3.5"),
                            new BigDecimal("3.75"), new BigDecimal("4"), new BigDecimal("5.5"))),

    /** Sick rates are fixed at 3.5 */
    SICK     (Arrays.asList(new BigDecimal("3.5"), new BigDecimal("3.5"), new BigDecimal("3.5"),
                            new BigDecimal("3.5"), new BigDecimal("3.5"), new BigDecimal("3.5")));

    private ArrayList<BigDecimal> accRates;

    AccrualRate(List<BigDecimal> accRates) {
        assert accRates.size() == 6;
        this.accRates = new ArrayList<>(accRates);
    }

    /**
     * Retrieve the accrual rate based on the payPeriods.
     * @param payPeriods int
     * @return BigDecimal with rate stored
     */
    public BigDecimal getRate(int payPeriods) {
        if (payPeriods <= 12) return accRates.get(0);
        if (payPeriods == 13) return accRates.get(1);
        if (payPeriods >= 14 && payPeriods <= 26) return accRates.get(2);
        if (payPeriods >= 27 && payPeriods <= 52) return accRates.get(3);
        if (payPeriods >= 53 && payPeriods <= 78) return accRates.get(4);
        else return accRates.get(5);
    }
}

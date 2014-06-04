package gov.nysenate.seta.model;

import gov.nysenate.seta.model.accrual.AccrualRate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccrualRateTests
{
    @Test
    public void testVacationRatesAreCorrect() throws Exception {
        assertEquals("0", AccrualRate.VACATION.getRate(3).toString());
        assertEquals("31.5", AccrualRate.VACATION.getRate(13).toString());
        assertEquals("3.5", AccrualRate.VACATION.getRate(14).toString());
        assertEquals("3.75", AccrualRate.VACATION.getRate(28).toString());
        assertEquals("4", AccrualRate.VACATION.getRate(53).toString());
        assertEquals("4", AccrualRate.VACATION.getRate(58).toString());
        assertEquals("5.5", AccrualRate.VACATION.getRate(89).toString());
    }

    @Test
    public void testSickRatesAreCorrect() throws Exception {
        assertEquals("3.5", AccrualRate.SICK.getRate(3).toString());
        assertEquals("3.5", AccrualRate.SICK.getRate(13).toString());
        assertEquals("3.5", AccrualRate.SICK.getRate(900).toString());
    }
}

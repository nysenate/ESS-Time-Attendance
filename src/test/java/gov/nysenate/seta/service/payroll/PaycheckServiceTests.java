package gov.nysenate.seta.service.payroll;

import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.model.payroll.Paycheck;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class PaycheckServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(PaycheckServiceTests.class);

    @Autowired PaycheckService paycheckService;
    private final int empId = 11168;
    private final int year = 2015;
    private List<Paycheck> paychecks;

    @Before
    public void before() {
        paychecks = paycheckService.getEmployeePaychecksForYear(empId, year);
    }

    @Test
    public void serviceCorrectlyInitializesPaycheck() {
        assertTrue(paychecks.size() >= 1);
        for (Paycheck paycheck : paychecks) {
            assertThat(paycheck.getCheckDate().getYear(), is(year));
            assertTrue(paycheck.getPayRate() != null);
            assertTrue(paycheck.getEmpFullName() != null);
            assertTrue(paycheck.getEmpJobTitle() != null);
            assertTrue(paycheck.getAgencyCode().matches("[0-9]{5}")); // All Agency codes are 5 characters.
            assertTrue(paycheck.getLineNum().matches("[0-9]{5}")); // All Line numbers are 5 characters.
            assertTrue(paycheck.getPayPeriod().matches("[0-9]{1,2}")); //
            assertTrue(paycheck.getGrossIncome() != null);
            assertTrue(paycheck.getNetIncome() != null);
            assertTrue(paycheck.getDeductions() != null);
            assertTrue(paycheck.getDirectDepositAmount() != null);
            assertTrue(paycheck.getCheckAmount() != null);
        }
    }

    @Test
    public void sumOfDirectDepositsAndCheckShouldEqualNetIncome() {
        Paycheck paycheck = paychecks.get(0);
        assertThat(paycheck.getDirectDepositAmount().add(paycheck.getCheckAmount()), is(paycheck.getNetIncome()));
    }

    @Test
    public void grossIncomeMinusDeductionsShouldEqualNetIncome() {
        Paycheck paycheck = paychecks.get(0);
        assertTrue("Gross Income: " + paycheck.getGrossIncome() + " - " + paycheck.getTotalDeductions() + " != " + paycheck.getNetIncome(),
                   paycheck.getGrossIncome().subtract(paycheck.getTotalDeductions()).equals(paycheck.getNetIncome()));
    }
}

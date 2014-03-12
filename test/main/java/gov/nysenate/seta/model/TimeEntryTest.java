package gov.nysenate.seta.model;

import gov.nysenate.seta.AbstractContextTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by riken on 3/11/14.
 */
public class TimeEntryTest extends AbstractContextTests {

    @Test
    public void testGetDailyTotal() throws Exception {
        BigDecimal bd = new BigDecimal("0.00");
        Assert.assertEquals(0.0,bd);
    }
}

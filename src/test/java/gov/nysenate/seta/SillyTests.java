package gov.nysenate.seta;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.seta.dao.transaction.TransDaoOption;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.util.DateUtils;
import org.junit.Test;
import org.omg.CORBA.TRANSACTION_MODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

/**
 * A sample file to run misc tests.
 */
public class SillyTests
{
   private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

   public void test() {
       Map<String, String> values = new HashMap<String, String>();
       values.put("val", "value");
       //Map<String, String> values2 = (HashMap<String, String>)DeepCopy.copy(values);
       Map<String, String> values2 = new HashMap<String, String>(values);
       values.put("val", "TEST");

       if (values.get("val").equals(values2.get("val"))) {
           System.out.println("value2 is equal to value ("+values2.get("val")+")");
       }
       else {
           System.out.println("value2 DOES NOT equal to value ("+values.get("val")+", "+values2.get("val")+")");
       }
   }

    @Test
    public void testUUID() throws Exception {
        logger.debug("{}", UUID.fromString("db551bca-0882-4a58-b3b3-8b2b8c27768a"));
        Set<TransactionCode> hashSet = Sets.newHashSet(TransactionCode.APP, null);
        logger.info("{}", Sets.intersection(null, TransactionCode.getAll()));
    }

    public static void main(String[] args) {
      SillyTests sillyTests = new SillyTests();
      sillyTests.test();
    }

    @Test
    public void testInt() throws Exception {
        Integer i = Integer.valueOf("5");
        logger.info("{}", i);
        Range<LocalDate> dateRange = Range.lessThan(LocalDate.of(2014, 1, 12));
        LocalDate date = LocalDate.now();
        date.atStartOfDay().plusHours(2);
        logger.info("{}", dateRange);
    }

    @Test
    public void testSomething() throws Exception {
        TreeMap<LocalDate, String> testMap = new TreeMap<>();
        testMap.put(LocalDate.of(2013, 1, 1), "First");
        testMap.put(LocalDate.of(2013, 3, 1), "Second");
        testMap.put(LocalDate.of(2013, 6, 1), "Third");
        testMap.put(LocalDate.of(2013, 9, 1), "Fourth");
        testMap.put(LocalDate.of(2014, 3, 1), "Fifth");

        logger.info("{}", testMap.floorEntry(LocalDate.of(2013, 7, 1)));

        logger.info("{}", TransactionCode.SAL.getDbColumnList());

    }
}

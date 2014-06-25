package gov.nysenate.seta;

import gov.nysenate.seta.util.DeepCopy;

import java.util.HashMap;
import java.util.Map;

/**
 * A sample file to run misc tests.
 */
public class SillyTests
{
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

  public static void main(String[] args) {
      SillyTests sillyTests = new SillyTests();
      sillyTests.test();
  }
}

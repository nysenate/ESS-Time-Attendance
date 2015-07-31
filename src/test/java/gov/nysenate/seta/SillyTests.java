package gov.nysenate.seta;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A sample file to run misc tests.
 */
public class SillyTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    @Test
    public void listTest() {
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4);
        Iterator<Integer> it = list.iterator();
        it.next();
        while (it.hasNext()) {
            logger.info("{}", it.next());
        }
    }
}

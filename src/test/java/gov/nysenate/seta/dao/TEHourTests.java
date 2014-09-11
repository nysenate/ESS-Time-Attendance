package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.allowances.AllowanceDao;
import gov.nysenate.seta.dao.allowances.SqlTEHoursDao;
import gov.nysenate.seta.dao.allowances.TEHoursDao;
import gov.nysenate.seta.model.allowances.TEHours;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by heitner on 7/28/2014.
 */
public class TEHourTests extends AbstractContextTests {

    private static final Logger logger = LoggerFactory.getLogger(SqlAllowanceDaoTests.class);

    @Autowired
    private SqlTEHoursDao tEHoursDao;

    @Test
    public void testTeHours() {
        logger.debug("Before getting records");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        ArrayList<TEHours> teHourses = tEHoursDao.getTEHours(11225, 2014);
        for (TEHours curTEHours : teHourses) {
            logger.debug("Current TE Hours:"+sdf.format(curTEHours.getBeginDate())+" - "+sdf.format(curTEHours.getEndDate())+": "+curTEHours.getTEHours());
        }
        logger.debug("Record Count:"+teHourses.size());
        logger.debug("total:"+tEHoursDao.sumTEHours(teHourses).getTEHours());
    }

}

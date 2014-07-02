package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.seta.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SqlEmployeeDaoTests extends AbstractContextTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDaoTests.class);

    @Autowired
    private EmployeeDao employeeDao;

    @Test
    public void testGetEmployeeById_validIdReturnsEmployee() throws Exception {
        int validId = 1446;
        Employee emp = employeeDao.getEmployeeById(validId);
        assertNotNull(emp);
        assertEquals(validId, emp.getEmployeeId());
        logger.info(OutputUtils.toJson(emp));
    }

    @Test
    public void testGetEmployeeByEmail_validIdReturnsEmployee() throws Exception {
        String validEmail = "crain@nysenate.gov";
        Employee emp = employeeDao.getEmployeeByEmail(validEmail);
        assertNotNull(emp);
        assertEquals(validEmail, emp.getEmail());
        logger.info(OutputUtils.toJson(emp));
    }

    @Test(expected = EmployeeNotFoundEx.class)
    public void testGetEmployeeById_invalidIdThrowsEmployeeNotFoundEx() throws Exception {
        assertNotNull(employeeDao);
        int invalidEmpId = 999999;
        Employee emp = employeeDao.getEmployeeById(invalidEmpId);
    }

    @Test(expected = EmployeeNotFoundEx.class)
    public void testGetEmployeeByEmail_invalidEmailThrowsEmployeeNotFoundEx() throws Exception {
        assertNotNull(employeeDao);
        String invalidEmail = "moose@kitten.com";
        Employee emp = employeeDao.getEmployeeByEmail(invalidEmail);
    }

    @Test
    public void testGetActiveEmployess_returnsUniqueEmployeeList() throws Exception {
        Map<Integer, Employee> dupMapCheck = new HashMap<>();
        Set<Integer> dups = new HashSet<>();
    /*    List<Employee> employees = employeeDao.getActiveEmployeesDuring()
        assertNotNull(employees);
        assertNotNull(employees.get(0));
        for (Employee e : employees) {
            if (dupMapCheck.get(e.getEmployeeId()) != null) {
                dups.add(e.getEmployeeId());
            }
            else {
                dupMapCheck.put(e.getEmployeeId(), e);
            }
        }
        logger.info("Duplicates: " + dups);
        assertTrue("Duplicate employees records exist!", dups.size() == 0);
        logger.info("Employee count: " + employees.size());      */
    }

    @Test
    public void testGetActiveEmployeeMap_returnsMap() throws Exception {
      //  Map<Integer, Employee> map = employeeDao.getActiveEmployeeMap();
    //    assertNotNull(map);
    //    assertTrue("Map is empty!", map.size() > 1);
       // assertTrue("Map size is not the same as employee list size!", map.size() == employeeDao.getActiveEmployees().size());
    //    logger.info("Map size: " + map.size());
    }

    @Test
    public void testGetEmployeeByIdAndDate_returnsCorrectSnapshot() throws Exception {
        //Employee then = employeeDao.getEmployeeById(6221, new DateTime(2008, 12, 31, 0, 0, 0).toDate());
        //Employee now = employeeDao.getEmployeeById(6221);
        //logger.info("Then: " + OutputUtils.toJson(then));
        //logger.info("Now: " + OutputUtils.toJson(now));

    }
}

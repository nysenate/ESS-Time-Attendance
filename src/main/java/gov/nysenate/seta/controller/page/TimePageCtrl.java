package gov.nysenate.seta.controller.page;

import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.auth.SenateLdapPerson;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.service.personnel.EmployeeInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Handles requests to the Time and Attendance page.
 */
@Controller
@RequestMapping("/time/**")
public class TimePageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TimePageCtrl.class);

    @Autowired EmployeeInfoService empInfoService;

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap modelMap, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            SenateLdapPerson senateLdapPerson = (SenateLdapPerson) subject.getPrincipal();
            modelMap.put("principal", senateLdapPerson);
            modelMap.put("principalJson", OutputUtils.toJson(senateLdapPerson));
            List<Integer> employeeActiveYears =
                    empInfoService.getEmployeeActiveYearsService(Integer.parseInt(senateLdapPerson.getEmployeeId()));
            modelMap.put("empActiveYears", OutputUtils.toJson(employeeActiveYears));
        }
		return "time";
	}
}
package gov.nysenate.seta.controller.page;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests to the Time and Attendance page.
 */
@Controller
@RequestMapping("/time/**")
public class TimePageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TimePageCtrl.class);

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap modelMap, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            modelMap.put("principal", subject.getPrincipal().toString());
        }
		return "time";
	}
}
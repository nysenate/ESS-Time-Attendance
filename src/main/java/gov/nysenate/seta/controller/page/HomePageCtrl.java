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

@Controller
@RequestMapping("/")
public class HomePageCtrl {

    private static final Logger logger = LoggerFactory.getLogger(HomePageCtrl.class);

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap modelMap, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            modelMap.put("principal", subject.getPrincipal().toString());
        }
		return "home";
	}

    @RequestMapping(value = "ui/**", method = RequestMethod.GET)
    public String home() {
        return "home";
    }
}
package gov.nysenate.seta.controller.page;

import gov.nysenate.common.OutputUtils;
import gov.nysenate.seta.model.auth.SenateLdapPerson;
import gov.nysenate.seta.service.personnel.EmployeeInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class BaseEssPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BaseEssPageCtrl.class);

    @Autowired EmployeeInfoService empInfoService;

    abstract String mainPage(ModelMap modelMap, HttpServletRequest request);

    protected void addModelMapData(ModelMap modelMap) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            SenateLdapPerson senateLdapPerson = (SenateLdapPerson) subject.getPrincipal();
            modelMap.put("principal", senateLdapPerson);
            modelMap.put("principalJson", OutputUtils.toJson(senateLdapPerson));
            List<Integer> employeeActiveYears =
                    empInfoService.getEmployeeActiveYearsService(Integer.parseInt(senateLdapPerson.getEmployeeId()));
            modelMap.put("empActiveYears", OutputUtils.toJson(employeeActiveYears));
        }
    }
}
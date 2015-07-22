package gov.nysenate.seta.controller.rest;

import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.SimpleResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.view.DetailedEmployeeView;
import gov.nysenate.seta.client.view.EmployeeView;
import gov.nysenate.seta.dao.personnel.EmployeeDao;
import gov.nysenate.seta.model.personnel.EmployeeException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;

@RestController
@RequestMapping(REST_PATH + "/employees")
public class EmployeeRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRestCtrl.class);

    @Autowired protected EmployeeDao employeeDao;

    @RequestMapping(value = "/empId/{empId}")
    public BaseResponse getEmployeeById(@PathVariable Integer empId,
                                        @RequestParam(defaultValue = "false") boolean detail) throws EmployeeException {
        Subject subject = getSubject();
//        subject.checkPermission("api:employees:view:" + empId);
        return (detail) ? new ViewObjectResponse<>(new DetailedEmployeeView(employeeDao.getEmployeeById(empId)))
                        : new ViewObjectResponse<>(new EmployeeView(employeeDao.getEmployeeById(empId)));
    }

    @RequestMapping("/email/{email}")
    public BaseResponse getEmployeeByEmail(@PathVariable String email,
                                           @RequestParam(defaultValue = "false") boolean detail) throws EmployeeException {
        return (detail) ? new ViewObjectResponse<>(new DetailedEmployeeView(employeeDao.getEmployeeByEmail(email)))
                        : new ViewObjectResponse<>(new EmployeeView(employeeDao.getEmployeeByEmail(email)));
    }
}
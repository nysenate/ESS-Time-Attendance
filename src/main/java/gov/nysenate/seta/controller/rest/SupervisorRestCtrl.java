package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.response.error.ErrorCode;
import gov.nysenate.seta.client.response.error.ErrorResponse;
import gov.nysenate.seta.client.view.SupervisorChainView;
import gov.nysenate.seta.client.view.SupervisorEmpGroupView;
import gov.nysenate.seta.client.view.SupervisorGrantView;
import gov.nysenate.seta.client.view.SupervisorOverrideView;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.SupervisorChain;
import gov.nysenate.seta.model.personnel.SupervisorOverride;
import gov.nysenate.seta.service.personnel.EmployeeInfoService;
import gov.nysenate.seta.service.personnel.SupervisorInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(BaseRestCtrl.REST_PATH + "/supervisor")
public class SupervisorRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupervisorRestCtrl.class);

    @Autowired private EmployeeInfoService empInfoService;
    @Autowired private SupervisorInfoService supInfoService;

    @RequestMapping(value = "/employees")
    public BaseResponse getSupervisorEmployees(@RequestParam Integer supId,
                                               @RequestParam(required = false) String fromDate,
                                               @RequestParam(required = false) String toDate) {
        LocalDate fromLocalDate = (fromDate != null) ? parseISODate(fromDate, "from date") : DateUtils.LONG_AGO;
        LocalDate toLocalDate = (toDate != null) ? parseISODate(toDate, "to date") : DateUtils.THE_FUTURE;
        try {
            return new ViewObjectResponse<>(
                new SupervisorEmpGroupView(
                    supInfoService.getSupervisorEmpGroup(supId, Range.closed(fromLocalDate, toLocalDate))));
        }
        catch (SupervisorException e) {
            return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
        }
    }

    @RequestMapping(value = "/chain")
    public BaseResponse getSupervisorChain(@RequestParam Integer empId,
                                           @RequestParam(required = false) String date) {
        LocalDate localDate = (date != null) ? parseISODate(date, "date") : LocalDate.now();
        try {
            SupervisorChain supervisorChain = supInfoService.getSupervisorChain(empId, localDate, 3);
            Map<Integer, Employee> empMap = supervisorChain.getChain().stream()
                    .map(empInfoService::getEmployee)
                    .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
            return new ViewObjectResponse<>(
                new SupervisorChainView(supervisorChain, empMap)
            );
        }
        catch (SupervisorException e) {
            return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
        }
    }

    @RequestMapping(value = "/overrides")
    public BaseResponse getSupervisorOverrides(@RequestParam Integer supId) {
        try {
            List<SupervisorOverride> overrides = supInfoService.getSupervisorOverrides(supId);
            return ListViewResponse.of(overrides.stream()
                    .map(ovr -> new SupervisorOverrideView(ovr, empInfoService.getEmployee(ovr.getOverrideSupervisorId())))
                    .collect(toList()), "overrides");
        }
        catch (SupervisorException e) {
            return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
        }
    }

    @RequestMapping(value = "/grants")
    public BaseResponse getSupervisorGrants(@RequestParam Integer supId) {
        try {
            List<SupervisorOverride> overrides = supInfoService.getSupervisorGrants(supId);
            return ListViewResponse.of(overrides.stream()
                    .map(ovr -> new SupervisorGrantView(ovr, empInfoService.getEmployee(ovr.getSupervisorId())))
                    .collect(toList()), "grants");
        }
        catch (SupervisorException e) {
            return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
        }
    }
}
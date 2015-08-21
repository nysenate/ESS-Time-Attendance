package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.response.error.ErrorCode;
import gov.nysenate.seta.client.response.error.ErrorResponse;
import gov.nysenate.seta.client.view.SupervisorEmpGroupView;
import gov.nysenate.seta.model.exception.SupervisorException;
import gov.nysenate.seta.service.personnel.SupervisorInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(BaseRestCtrl.REST_PATH + "/supervisor")
public class SupervisorRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupervisorRestCtrl.class);

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
}
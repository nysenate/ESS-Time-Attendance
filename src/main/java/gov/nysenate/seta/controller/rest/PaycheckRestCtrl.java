package gov.nysenate.seta.controller.rest;

import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.view.PaycheckView;
import gov.nysenate.seta.model.payroll.Paycheck;
import gov.nysenate.seta.service.payroll.PaycheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(REST_PATH + "/paychecks")
public class PaycheckRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(PaycheckRestCtrl.class);

    @Autowired PaycheckService paycheckService;

    @RequestMapping(value = "", params = {"empId", "year"})
    public BaseResponse getPaychecksByYear(@RequestParam Integer empId, @RequestParam Integer year, WebRequest webRequest) {
        List<Paycheck> paychecks = paycheckService.getEmployeePaychecksForYear(empId, year);
        return ListViewResponse.of(paychecks.stream().map(PaycheckView::new).collect(toList()), "paycheck");
    }

}

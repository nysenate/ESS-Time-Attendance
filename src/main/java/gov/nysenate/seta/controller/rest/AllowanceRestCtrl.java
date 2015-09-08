package gov.nysenate.seta.controller.rest;

import com.google.common.collect.Sets;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.view.AllowanceUsageView;
import gov.nysenate.seta.service.allowance.AllowanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;

@RestController
@RequestMapping(REST_PATH + "/allowances")
public class AllowanceRestCtrl extends BaseRestCtrl {

    private static final Logger logger = LoggerFactory.getLogger(AllowanceRestCtrl.class);

    @Autowired AllowanceService allowanceService;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ListViewResponse getAllowances(@RequestParam Integer[] empId,
                                          @RequestParam Integer[] year) {
        Set<Integer> empIds = Sets.newHashSet(empId);
        Set<Integer> years = Sets.newHashSet(year);
        return ListViewResponse.of(
                empIds.stream()
                        .flatMap(eId -> years.stream()
                                .map(yr -> allowanceService.getAllowanceUsage(eId, yr)))
                        .map(AllowanceUsageView::new)
                        .collect(Collectors.toList())
        );
    }
}

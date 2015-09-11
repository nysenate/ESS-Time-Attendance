package gov.nysenate.seta.controller.rest;

import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.response.base.ViewObjectResponse;
import gov.nysenate.seta.client.view.EmpTransItemView;
import gov.nysenate.seta.client.view.EmpTransRecordView;
import gov.nysenate.seta.client.view.base.MapView;
import gov.nysenate.seta.model.transaction.TransactionCode;
import gov.nysenate.seta.service.transaction.EmpTransactionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(REST_PATH + "/empTransactions")
public class EmpTransactionRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EmpTransactionRestCtrl.class);

    @Autowired private EmpTransactionService transactionService;

    @RequestMapping("")
    public BaseResponse getTransactionsByEmpId(@RequestParam Integer empId,
                                               @RequestParam(required = false) String fromDate,
                                               @RequestParam(required = false) String toDate,
                                               @RequestParam(required = false) String codes) {
        LocalDate fromLocalDate = (fromDate != null) ? parseISODate(fromDate, "from-date") : DateUtils.LONG_AGO;
        LocalDate toLocalDate = (toDate != null) ? parseISODate(toDate, "to-date") : DateUtils.THE_FUTURE;
        Range<LocalDate> range = Range.closed(fromLocalDate, toLocalDate);
        Set<TransactionCode> codeSet = getTransCodesFromString(codes);
        return ListViewResponse.of(
            transactionService.getTransHistory(empId)
                .getTransRecords(range, codeSet, SortOrder.ASC).stream()
                .map(EmpTransRecordView::new)
                .collect(toList()), "transactions");
    }

    @RequestMapping("/snapshot")
    public BaseResponse getFlatTransactionByEmpId(@RequestParam Integer empId,
                                                  @RequestParam(required = false) String date) {
        LocalDate localDate = (date != null) ? parseISODate(date, "date") : LocalDate.now();
        Map<String, EmpTransItemView> itemMap = new HashMap<>();
        transactionService.getTransHistory(empId).getRecordSnapshots()
            .floorEntry(localDate).getValue().entrySet().stream().forEach(e -> {
            itemMap.put(e.getKey(), new EmpTransItemView(e.getKey(), e.getValue()));
        });
        return new ViewObjectResponse<>(MapView.of(itemMap), "snapshot");
    }

    private Set<TransactionCode> getTransCodesFromString(String codes) {
        if (StringUtils.isNotBlank(codes)) {
            List<String> codeStrList = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(codes);

            Set<TransactionCode> codeSet = new HashSet<>();
            for (String code : codeStrList) {
                try {
                    codeSet.add(TransactionCode.valueOf(code));
                }
                catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException(code + " is not a valid transaction code.");
                }
            }
            return codeSet;
        }
        return TransactionCode.getAll();
    }
}
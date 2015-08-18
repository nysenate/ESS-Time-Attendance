package gov.nysenate.seta.controller.rest;

import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import gov.nysenate.common.DateUtils;
import gov.nysenate.common.SortOrder;
import gov.nysenate.seta.client.response.base.BaseResponse;
import gov.nysenate.seta.client.response.base.ListViewResponse;
import gov.nysenate.seta.client.view.EmpTransRecordView;
import gov.nysenate.seta.dao.transaction.EmpTransDaoOption;
import gov.nysenate.seta.dao.transaction.EmpTransactionDao;
import gov.nysenate.seta.model.transaction.TransactionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gov.nysenate.seta.controller.rest.BaseRestCtrl.REST_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(REST_PATH + "/empTransactions")
public class EmpTransactionRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EmpTransactionRestCtrl.class);

    @Autowired private EmpTransactionDao empTransactionDao;

    @RequestMapping("")
    public BaseResponse getTransactionsByEmpId(@RequestParam Integer empId,
                                               @RequestParam(required = false) String fromDate,
                                               @RequestParam(required = false) String toDate,
                                               WebRequest request) {
        LocalDate fromLocalDate = (fromDate != null) ? parseISODate(fromDate, "from-date") : DateUtils.LONG_AGO;
        LocalDate toLocalDate = (toDate != null) ? parseISODate(toDate, "to-date") : DateUtils.THE_FUTURE;
        Range<LocalDate> range = Range.closed(fromLocalDate, toLocalDate);
        Set<TransactionCode> codeSet = getCodesFromParam(request);
        return ListViewResponse.of(
            empTransactionDao.getTransHistory(empId, codeSet, range, EmpTransDaoOption.DEFAULT)
                .getAllTransRecords(SortOrder.ASC).stream()
                    .map(EmpTransRecordView::new)
                    .collect(toList()), "transactions");
    }

    private Set<TransactionCode> getCodesFromParam(WebRequest request) {
        if (request.getParameter("codes") != null) {
            List<String> codeStrList = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(request.getParameter("codes"));

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
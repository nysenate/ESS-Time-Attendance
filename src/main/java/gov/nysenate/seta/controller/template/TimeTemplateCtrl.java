package gov.nysenate.seta.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for front-end templates associated with time/attendance functionality.
 */
@Controller
@RequestMapping(TimeTemplateCtrl.TIME_TMPL_BASE_URL)
public class TimeTemplateCtrl extends BaseTemplateCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TimeTemplateCtrl.class);
    protected static final String TIME_TMPL_BASE_URL = TMPL_BASE_URL + "/time";

    /** --- Records --- */

    @RequestMapping(value="/record/entry")
    public String entry() {
        return TIME_TMPL_BASE_URL + "/record/entry";
    }

    @RequestMapping(value="/record/history")
    public String history() {
        return TIME_TMPL_BASE_URL + "/record/history";
    }

    @RequestMapping(value="/record/manage")
    public String manage() {
        return TIME_TMPL_BASE_URL + "/record/manage";
    }

    @RequestMapping(value="/record/emphistory")
    public String employeeHistory() {
        return TIME_TMPL_BASE_URL + "/record/emp-history";
    }

    @RequestMapping(value="/record/grant")
    public String grant() {
        return TIME_TMPL_BASE_URL + "/record/grant";
    }

    /** --- Accruals --- */

    @RequestMapping(value="/accrual/history")
    public String accrualHistory() {
        return TIME_TMPL_BASE_URL + "/accrual/history";
    }

    @RequestMapping(value="/accrual/projections")
    public String accrualProjections() {
        return TIME_TMPL_BASE_URL + "/accrual/projections";
    }

    /** --- Time Off --- */

    @RequestMapping(value="/timeoff/request")
    public String timeOffRequest() {
        return TIME_TMPL_BASE_URL + "/timeoff/request";
    }

    /** --- Calendar --- */

    @RequestMapping(value="/period/calendar")
    public String payPeriodView() {
        return TIME_TMPL_BASE_URL + "/period/calendar";
    }
}
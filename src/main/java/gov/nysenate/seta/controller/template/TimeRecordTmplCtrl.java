package gov.nysenate.seta.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for front-end templates associated with time record functionality.
 */
@Controller
@RequestMapping(TimeRecordTmplCtrl.RECORD_TMPL_BASE_URL)
public class TimeRecordTmplCtrl extends BaseTmplCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TimeRecordTmplCtrl.class);
    protected static final String RECORD_TMPL_BASE_URL = TMPL_BASE_URL + "/record";

    @RequestMapping(value="/entry")
    public String entry() {
        return RECORD_TMPL_BASE_URL + "/entry";
    }

    @RequestMapping(value="/history")
    public String history() {
        return RECORD_TMPL_BASE_URL + "/history";
    }

    @RequestMapping(value="/timeoff")
    public String timeOff() {
        return RECORD_TMPL_BASE_URL + "/timeoff";
    }

    @RequestMapping(value="/manage")
    public String manage() {
        return RECORD_TMPL_BASE_URL + "/manage";
    }
}

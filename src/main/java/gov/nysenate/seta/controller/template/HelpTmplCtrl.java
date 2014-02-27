package gov.nysenate.seta.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for front-end templates associated with help/documentation.
 */
@Controller
@RequestMapping(HelpTmplCtrl.HELP_TMPL_BASE_URL)
public class HelpTmplCtrl extends BaseTmplCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(HelpTmplCtrl.class);
    protected static final String HELP_TMPL_BASE_URL = TMPL_BASE_URL + "/help";
}

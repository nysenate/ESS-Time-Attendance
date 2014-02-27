package gov.nysenate.seta.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for front-end templates associated with dashboard functionality.
 */
@Controller
@RequestMapping(DashboardTmplCtrl.DASHBOARD_TMPL_BASE_URL)
public class DashboardTmplCtrl extends BaseTmplCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(DashboardTmplCtrl.class);
    protected static final String DASHBOARD_TMPL_BASE_URL = TMPL_BASE_URL + "/dashboard";
}

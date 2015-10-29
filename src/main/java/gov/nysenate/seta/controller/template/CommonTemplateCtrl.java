package gov.nysenate.seta.controller.template;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static gov.nysenate.seta.controller.template.BaseTemplateCtrl.TMPL_BASE_URL;

@Controller
@RequestMapping(TMPL_BASE_URL)
public class CommonTemplateCtrl extends BaseTemplateCtrl
{
    @RequestMapping("/404")
    public String pageNotFound() {
        return TMPL_BASE_URL + "/base/404";
    }
}

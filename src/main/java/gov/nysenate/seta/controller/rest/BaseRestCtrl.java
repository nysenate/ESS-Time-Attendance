package gov.nysenate.seta.controller.rest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class BaseRestCtrl
{
    protected Subject getSubject() {
        return SecurityUtils.getSubject();
    }
}

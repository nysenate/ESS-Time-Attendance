package gov.nysenate.seta.controller.rest;

import gov.nysenate.seta.client.response.record.RecordEntryInfoResponse;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TestRestCtrl.class);

    @RequestMapping(value = "/rest/v1/record/entry/{uid}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public @ResponseBody
    RecordEntryInfoResponse viewRecordEntryInfo(@PathVariable String uid) {
        String permission = "record:entry:view:" + uid;
        Subject subject = getSubject();
        //subject.checkPermission(permission);
        return new RecordEntryInfoResponse();
    }
}

package gov.nysenate.seta.model;

import java.util.Date;

public class ResponsibilityCenter
{
    protected boolean active;
    protected int code;
    protected String name;
    protected Date effectiveDateBegin;
    protected Date effectiveDateEnd;
    protected Agency agency;
    protected ResponsibilityHead head;

    public ResponsibilityCenter() {}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEffectiveDateBegin() {
        return effectiveDateBegin;
    }

    public void setEffectiveDateBegin(Date effectiveDateBegin) {
        this.effectiveDateBegin = effectiveDateBegin;
    }

    public Date getEffectiveDateEnd() {
        return effectiveDateEnd;
    }

    public void setEffectiveDateEnd(Date effectiveDateEnd) {
        this.effectiveDateEnd = effectiveDateEnd;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public ResponsibilityHead getHead() {
        return head;
    }

    public void setHead(ResponsibilityHead head) {
        this.head = head;
    }
}
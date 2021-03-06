package gov.nysenate.seta.model.personnel;

import java.time.LocalDate;

/**
 * A responsibility center is basically an organizational unit that is headed by a manager.
 * The ResponsibilityCenter is primarily defined by the agency and resp center head codes and
 * therefore contains those objects as well.
 */
public class ResponsibilityCenter
{
    protected boolean active;
    protected int code;
    protected String name;
    protected LocalDate effectiveDateBegin;
    protected LocalDate effectiveDateEnd;
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

    public LocalDate getEffectiveDateBegin() {
        return effectiveDateBegin;
    }

    public void setEffectiveDateBegin(LocalDate effectiveDateBegin) {
        this.effectiveDateBegin = effectiveDateBegin;
    }

    public LocalDate getEffectiveDateEnd() {
        return effectiveDateEnd;
    }

    public void setEffectiveDateEnd(LocalDate effectiveDateEnd) {
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
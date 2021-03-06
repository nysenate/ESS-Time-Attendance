package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.personnel.SupervisorOverride;

import java.time.LocalDate;

public class SupervisorGrantSimpleView implements ViewObject
{
    protected int granterSupervisorId;
    protected int granteeSupervisorId;
    protected boolean active;
    protected LocalDate startDate;
    protected LocalDate endDate;

    /** --- Constructors --- */

    public SupervisorGrantSimpleView() {}

    public SupervisorGrantSimpleView(SupervisorOverride ovr) {
        if (ovr != null) {
            this.granteeSupervisorId = ovr.getGranteeSupervisorId();
            this.granterSupervisorId = ovr.getGranterSupervisorId();
            this.active = ovr.isActive();
            this.startDate = ovr.getStartDate().orElse(null);
            this.endDate = ovr.getEndDate().orElse(null);
        }
    }

    @Override
    public String getViewType() {
        return "supervisor grant";
    }

    /** --- Basic Getters --- */

    public int getGranteeSupervisorId() {
        return granteeSupervisorId;
    }

    public boolean isActive() {
        return active;
    }

    public int getGranterSupervisorId() {
        return granterSupervisorId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}

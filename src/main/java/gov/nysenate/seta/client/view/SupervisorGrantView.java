package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.SupervisorOverride;

import java.time.LocalDate;

/**
 * Similar to SupervisorOverrideView, just reversed.
 */
public class SupervisorGrantView implements ViewObject
{
    protected int supervisorId;
    protected boolean active;
    protected int grantSupervisorId;
    protected EmployeeView grantSupervisor;
    protected LocalDate startDate;
    protected LocalDate endDate;

    /** --- Constructors --- */

    public SupervisorGrantView(SupervisorOverride ovr, Employee grantSupervisor) {
        if (ovr != null) {
            this.supervisorId = ovr.getOverrideSupervisorId();
            this.grantSupervisorId = ovr.getSupervisorId();
            this.grantSupervisor = new EmployeeView(grantSupervisor);
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

    public int getSupervisorId() {
        return supervisorId;
    }

    public boolean isActive() {
        return active;
    }

    public int getGrantSupervisorId() {
        return grantSupervisorId;
    }

    public EmployeeView getGrantSupervisor() {
        return grantSupervisor;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}

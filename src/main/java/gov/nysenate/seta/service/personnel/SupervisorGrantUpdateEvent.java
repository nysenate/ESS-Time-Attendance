package gov.nysenate.seta.service.personnel;

import gov.nysenate.seta.model.personnel.SupervisorOverride;
import gov.nysenate.seta.service.base.BaseEvent;

public class SupervisorGrantUpdateEvent extends BaseEvent
{
    private SupervisorOverride supervisorOverride;

    public SupervisorGrantUpdateEvent(SupervisorOverride supervisorOverride) {
        this.supervisorOverride = supervisorOverride;
    }

    public SupervisorOverride getSupervisorOverride() {
        return supervisorOverride;
    }
}

package gov.nysenate.seta.model.personnel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class SupervisorOverride
{
    protected int supervisorId;
    protected boolean active;
    protected int overrideSupervisorId;
    protected Optional<LocalDate> startDate;
    protected Optional<LocalDate> endDate;
    protected LocalDateTime originDate;
    protected LocalDateTime updateDate;

    /** --- Constructors --- */

    public SupervisorOverride() {}

    /** --- Basic Getters/Setters --- */

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getOverrideSupervisorId() {
        return overrideSupervisorId;
    }

    public void setOverrideSupervisorId(int overrideSupervisorId) {
        this.overrideSupervisorId = overrideSupervisorId;
    }

    public Optional<LocalDate> getStartDate() {
        return startDate;
    }

    public void setStartDate(Optional<LocalDate> startDate) {
        this.startDate = startDate;
    }

    public Optional<LocalDate> getEndDate() {
        return endDate;
    }

    public void setEndDate(Optional<LocalDate> endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getOriginDate() {
        return originDate;
    }

    public void setOriginDate(LocalDateTime originDate) {
        this.originDate = originDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}

package gov.nysenate.seta.model;

public class SupervisorMissingEmpsEx extends SupervisorException {

    public SupervisorMissingEmpsEx() {
    }

    public SupervisorMissingEmpsEx(String message) {
        super(message);
    }

    public SupervisorMissingEmpsEx(String message, Throwable cause) {
        super(message, cause);
    }
}
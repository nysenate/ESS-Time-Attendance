package gov.nysenate.seta.model.payroll;

import java.time.LocalDate;
import java.util.Date;

/**
 * A simple model for storing holiday details.
 */
public class Holiday
{
    protected LocalDate date;
    protected String name;
    protected boolean active;

    /** Questionable holidays are declared at the discretion of the senate. */
    protected boolean questionable;

    public Holiday() {}

    /** --- Basic Getters/Setters --- */

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isQuestionable() {
        return questionable;
    }

    public void setQuestionable(boolean questionable) {
        this.questionable = questionable;
    }
}

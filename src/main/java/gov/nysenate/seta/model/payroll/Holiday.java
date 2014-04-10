package gov.nysenate.seta.model.payroll;

import java.util.Date;

/**
 * A simple model for storing holiday details.
 */
public class Holiday
{
    protected Date date;
    protected String name;
    protected boolean active;
    protected boolean questionable;

    public Holiday() {}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

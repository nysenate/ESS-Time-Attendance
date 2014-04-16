package gov.nysenate.seta.model.accrual;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Helper class to store accrual usage sums for a given year. Note that this class does
 * not contain information about the hours accrued, just how much was used.
 */
public class AnnualAccrualUsage extends AccrualUsage
{
    protected Date latestStartDate;
    protected Date latestEndDate;

    public AnnualAccrualUsage() {}

    /** Functional Getters/Setters */

    public int getYear() {
        if (latestStartDate != null) {
            return new DateTime(latestStartDate).getYear();
        }
        throw new IllegalStateException("The latest start date was not set for accrual usage. Cannot retrieve year!");
    }

    /** Basic Getters/Setters */

    public Date getLatestStartDate() {
        return latestStartDate;
    }

    public void setLatestStartDate(Date latestStartDate) {
        this.latestStartDate = latestStartDate;
    }

    public Date getLatestEndDate() {
        return latestEndDate;
    }

    public void setLatestEndDate(Date latestEndDate) {
        this.latestEndDate = latestEndDate;
    }
}

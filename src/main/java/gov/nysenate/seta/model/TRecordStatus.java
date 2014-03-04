package gov.nysenate.seta.model;

import java.sql.Timestamp;

/**
 * Created by riken on 3/4/14.
 */
public class TRecordStatus {

    protected String tRecordStatusId;
    protected String details;
    protected int tOriginalUserId;
    protected int tUpdateUserId;
    protected Timestamp tOriginalDate;
    protected Timestamp tUpdateDate;
    protected String unlockedFor;
    protected int orderLevel;

    public String gettRecordStatusId() {
        return tRecordStatusId;
    }

    public void settRecordStatusId(String tRecordStatusId) {
        this.tRecordStatusId = tRecordStatusId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int gettOriginalUserId() {
        return tOriginalUserId;
    }

    public void settOriginalUserId(int tOriginalUserId) {
        this.tOriginalUserId = tOriginalUserId;
    }

    public int gettUpdateUserId() {
        return tUpdateUserId;
    }

    public void settUpdateUserId(int tUpdateUserId) {
        this.tUpdateUserId = tUpdateUserId;
    }

    public Timestamp gettOriginalDate() {
        return tOriginalDate;
    }

    public void settOriginalDate(Timestamp tOriginalDate) {
        this.tOriginalDate = tOriginalDate;
    }

    public Timestamp gettUpdateDate() {
        return tUpdateDate;
    }

    public void settUpdateDate(Timestamp tUpdateDate) {
        this.tUpdateDate = tUpdateDate;
    }

    public String getUnlockedFor() {
        return unlockedFor;
    }

    public void setUnlockedFor(String unlockedFor) {
        this.unlockedFor = unlockedFor;
    }

    public int getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(int orderLevel) {
        this.orderLevel = orderLevel;
    }

}

package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;
import gov.nysenate.seta.model.exception.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.exception.TimeRecordNotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by riken on 3/11/14.
 */
public interface MigrationDao extends BaseDao {

    /**
     * Retrieve Time Records using LastRecordId / Starting from previous Ending.
     * @param rowNumber - First entry number.
     * @param threshold - Number of last entry to be retrieved
     * @return List of Time Record Objects otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getRemoteTimeRecord(int rowNumber, int threshold) throws TimeRecordNotFoundException;

    /**
     * Retrieve Time Entries using timeRecordId
     *
     * @param rowNumber - First entry number.
     * @param threshold - Number of last entry to be retrieved
     * @throws TimeEntryNotFoundEx
     */
    public List<TimeEntry> getRemoteTimeEntry(int rowNumber, int threshold) throws TimeEntryNotFoundEx;

    /**
     * Retrieve and Update Time Records Method
     */
    public void MigrateTimeRecord() throws TimeRecordNotFoundException;

    /**
     * Retrieve and Update Time Entry Method
     */
    public void MigrateTimeEntry() throws TimeEntryNotFoundEx;


}

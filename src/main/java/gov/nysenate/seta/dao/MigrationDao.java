package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by riken on 3/11/14.
 */
public interface MigrationDao extends BaseDao {

    /**
     * Retrieve Time Records using LastRecordId / Starting from previous Ending.
     * @param lastRecordId - Last updated record of table
     * @return List of Time Record Objects otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getRemoteTimeRecord(BigInteger lastRecordId) throws TimeRecordNotFoundException;

    /**
     * Retrieve Time Entries using timeRecordId
     * @param timeRecordId - Time Record If for Time Entries
     * @return List of Time Entries Objects otherwise throws TimeEntryNotFoundException
     * @throws TimeEntryNotFoundEx
     */
    public List<TimeEntry> getRemoteTimeEntry(BigInteger timeRecordId) throws TimeEntryNotFoundEx;

    /**
     * Retrieve Last Updated DataId using DataType from TempDataStore Table
     * @param dataType - char - Type of Data Entry to be retrieved with Date as addition parameter
     * @return Unique id of data using DataType otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
    */
    public BigInteger getLastRecordId(char dataType, Timestamp lastDate) throws TimeRecordNotFoundException;

    /**
     * Insert LastRecord Data into TempDataStore Table
     * @param tempDataStore - TempDataStore - Object containing data to be inserted
     */
    public void setLastRecordId(TempDataStore tempDataStore);
}

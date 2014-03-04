package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TimeRecord;
import gov.nysenate.seta.model.TimeRecordNotFoundException;

import java.util.List;

/**
 * Created by riken on 3/4/14.
 */
public interface MigrationTimeRecordDao {

    /**
     * Retrieves data to be synced with SFMSTimeRecord
     * @return List of TimeRecord Objects otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> migrateSFMSTimeRecord() throws TimeRecordNotFoundException;

    /**
     * Retrieves updated data from SFMSTimeRecord
     * @return List of TimeRecord Object otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getUpdatedTimeRecord() throws TimeRecordNotFoundException;
}

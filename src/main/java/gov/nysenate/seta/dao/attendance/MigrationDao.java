package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeRecordAudit;
import gov.nysenate.seta.model.attendance.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.attendance.TimeRecordNotFoundException;

import java.util.List;

public interface MigrationDao extends BaseDao {

    /**
     * Retrieve Time Records using LastRecordId / Starting from previous Ending.
     * @param rowNumber - First entry number.
     * @param threshold - Number of last entry to be retrieved
     * @return List of Time Record Objects otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecordAudit> getRemoteTimeRecordAudit(int rowNumber, int threshold) throws TimeRecordNotFoundException;

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

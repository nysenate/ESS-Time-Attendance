package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.attendance.TimeEntry;
import gov.nysenate.seta.model.attendance.TimeEntryException;
import gov.nysenate.seta.model.attendance.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.attendance.TimeRecordNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TimeEntryDao extends BaseDao
{
    /**
     * Retrieve all time entries that belong to a specific time record.
     * @param timeRecordId int - Id of the parent TimeRecord
     * @return List<TimeEntry>
     * @throws TimeEntryException - TimeEntryNotFoundEx if no matching time entries were found
     */
    public List<TimeEntry> getTimeEntriesByRecordId(int timeRecordId) throws TimeEntryException;

    /**
     * Retrieve all time entries for a given employee.
     * @param empId - int Employee ID
     * @return Mapped List of Entry objects otherwise TimeEntryNotFoundException
     * @throws TimeEntryNotFoundEx
     * @throws TimeRecordNotFoundException
     */
    public Map<String, List<TimeEntry>> getTimeEntryByEmpId(int empId) throws TimeEntryNotFoundEx,
                                                                              TimeRecordNotFoundException;

    /**
     * Insert the given time entry.
     * @param tsd - TimeEntry class object containing data to be inserted
     * @return boolean, true if data successfully inserted, otherwise false.
     */
    public boolean setTimeEntry(TimeEntry tsd);

    /**
     * Update time entry using TimeEntry Object
     * @param tsd - TimeEntry class object containing data to be updated
     * @return boolean, true if data successfully updated, otherwise false.
     */
    public boolean updateTimeEntry(TimeEntry tsd);
  }

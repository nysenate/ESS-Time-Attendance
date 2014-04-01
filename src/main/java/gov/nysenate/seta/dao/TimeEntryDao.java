package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TimeEntry;
import gov.nysenate.seta.model.exception.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.exception.TimeRecordNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by riken on 3/11/14.
 */
public interface TimeEntryDao extends BaseDao {

    /**
     * Retrieve all time entries using particular timesheetId
     * @param timesheetId - int Timesheet ID
     * @return List of Entry objects otherwise TimeEntryNotFoundException
     * @throws TimeEntryNotFoundEx
     */
    public List<TimeEntry> getTimeEntryByTimesheet(int timesheetId) throws TimeEntryNotFoundEx;

    /**
     * Retrieve all time etries using particular empId mapped with TimesheetId
     *
     * @param empId - int Employee ID
     * @return Mapped List of Entry objects otherwise TimeEntryNotFoundException
     * @throws TimeEntryNotFoundEx
     */
    public Map<BigDecimal, List<TimeEntry>> getTimeEntryByEmpId(int empId) throws TimeEntryNotFoundEx,TimeRecordNotFoundException;

    /**
     * Insert time entry using TimeEntry Object
     * @param tsd - TimeEntry class object containing data to be inserted
     * @return Boolean value, true if data successfully inserted else false.
     */
    public boolean setTimeEntry(TimeEntry tsd);

    /**
     * Update time entry using TimeEntry Object
     * @param tsd - TimeEntry class object containing data to be updated
     * @return @return Boolean value, true if data successfully updated else false.
     */
    public boolean updateTimeEntry(TimeEntry tsd);
  }

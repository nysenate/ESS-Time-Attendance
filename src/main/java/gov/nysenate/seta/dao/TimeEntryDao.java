package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TimeEntry;
import gov.nysenate.seta.model.TimeEntryNotFoundEx;
import gov.nysenate.seta.model.TimeRecordNotFoundException;

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
     * @param empId - int Employee ID
     * @return Mapped List of Entry objects otherwise TimeEntryNotFoundException
     * @throws TimeEntryNotFoundEx
     */
    public Map<Integer,List<TimeEntry>> getTimeEntryByEmpId(int empId) throws TimeEntryNotFoundEx,TimeRecordNotFoundException;


  }

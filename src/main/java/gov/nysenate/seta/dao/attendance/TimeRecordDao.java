package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.attendance.TimeRecord;
import gov.nysenate.seta.model.attendance.TimeRecordNotFoundException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TimeRecordDao extends BaseDao
{
    /**
     * Retrieves a list of all TimeRecords for a given employee id.
     * @param empId int - EmployeeId
     * @return List of TimeRecord Objects
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getRecordByEmployeeId(int empId) throws TimeRecordNotFoundException;

    /**
     * Retrieves TimeRecord mapped with integer based on empIds given.
     * @param empIds int - list ofEmployeeId
     * @return TimeRecord Object if found otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */

    public Map<Integer,List<TimeRecord>> getRecordByEmployeeIdMap(List<Integer> empIds)throws TimeRecordNotFoundException;

    /**
     * Retrieves TimeRecord using PayPeriod
     * @param startDate Date - Starting Date
     * @param endDate Date - End Date
     * @return List of TimeRecord Objects if found otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getRecordByPayPeriod(Date startDate, Date endDate) throws TimeRecordNotFoundException;

    /**
     * Retrieves TimeRecord using TimesheetStatusId, EmployeeId and PayPeriod Combination
     * @param tSStatusId
     * @param empId
     * @param startDate
     * @param endDate
     * @return List of TimeRecord Objects if found otherwise throws TimeRecordNotFountException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getRecordByTSStatus(String tSStatusId, int empId, Date startDate, Date endDate) throws TimeRecordNotFoundException;

    /**
     * Add New TimeRecord to the Timesheet Table
     * @param tr - TimeRecord class object containing data to be inserted into the table
     * @return Boolean value, true if data successfully inserted else false.
     */
    public boolean setRecord(TimeRecord tr);

    /**
     * Update TimeRecord to the Timesheet Table
     * @param tr - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    public boolean updateRecord(TimeRecord tr);

    /**
     * Get Count of TimeRecord using timesheetId
     * @param timesheetId - Id of Timesheet
     * @return number of counts
     */
    public int getTimeRecordCount(BigDecimal timesheetId) throws TimeRecordNotFoundException;
}

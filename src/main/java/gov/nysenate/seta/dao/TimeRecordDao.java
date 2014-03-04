package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TimeRecord;
import gov.nysenate.seta.model.TimeRecordNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by riken on 3/4/14.
 */
public interface TimeRecordDao extends BaseDao {

    /**
     * Retrieves All TimeRecords based on EmpId passed.
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

    public Map<Integer,TimeRecord> getRecordByEmployeeIdMap(List<Integer> empIds)throws TimeRecordNotFoundException;

    /**
     * Retrieves TimeRecord using PayPeriod
     * @param startDate Date - Starting Date
     * @param endDate Date - End Date
     * @return List of TimeRecord Objects if found otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getRecordByPayPeriod(Date startDate, Date endDate) throws TimeRecordNotFoundException;

    /**
     * Retrieves TimeRecord using SupervisorId
     * @param supervisorId
     * @return List of TimeRecord Objects if found otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getRecordBySupervisorId(int supervisorId) throws TimeRecordNotFoundException;

    /**
     * Retrieves TimeRecord using Timesheet Status
     * @param tSStatusId
     * @return List of TimeRecord Objects if found otherwise throws TimeRecordNotFoundException
     * @throws TimeRecordNotFoundException
     */
    public List<TimeRecord> getRecordByTSStatus(int tSStatusId) throws TimeRecordNotFoundException;

    /**
     *
     * @return
     */
    public Boolean AddTimeRecord();

}

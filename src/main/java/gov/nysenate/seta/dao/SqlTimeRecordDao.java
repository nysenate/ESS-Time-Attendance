package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.TimeRecord;
import gov.nysenate.seta.model.TimeRecordNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by riken on 3/4/14.
 */
public class SqlTimeRecordDao implements TimeRecordDao{


    @Override
    public List<TimeRecord> getRecordByEmployeeId(int empId) throws TimeRecordNotFoundException {
        return null;
    }

    @Override
    public Map<Integer, TimeRecord> getRecordByEmployeeIdMap(List<Integer> empIds) throws TimeRecordNotFoundException {
        return null;
    }

    @Override
    public List<TimeRecord> getRecordByPayPeriod(Date startDate, Date endDate) throws TimeRecordNotFoundException {
        return null;
    }

    @Override
    public List<TimeRecord> getRecordBySupervisorId(int supervisorId) throws TimeRecordNotFoundException {
        return null;
    }

    @Override
    public List<TimeRecord> getRecordByTSStatus(int tSStatusId) throws TimeRecordNotFoundException {
        return null;
    }

    @Override
    public Boolean AddTimeRecord() {
        return null;
    }
}

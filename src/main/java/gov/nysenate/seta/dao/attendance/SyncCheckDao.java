package gov.nysenate.seta.dao.attendance;


import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.attendance.SyncCheck;

/**
 * Created by riken on 4/8/14.
 */
public interface SyncCheckDao extends BaseDao {

    /**
     * Insert new record to SyncCheck Table
     * @param sc - Object Containing data to be inserted
     * @return true if data successfully inserted else false
     */
    public boolean setSyncData(SyncCheck sc);
}

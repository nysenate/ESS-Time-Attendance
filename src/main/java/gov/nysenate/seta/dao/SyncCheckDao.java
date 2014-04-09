package gov.nysenate.seta.dao;


import gov.nysenate.seta.model.SyncCheck;
import org.springframework.stereotype.Repository;

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

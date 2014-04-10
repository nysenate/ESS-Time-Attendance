package gov.nysenate.seta.dao.attendance;

import gov.nysenate.seta.dao.base.SqlBaseDao;
import gov.nysenate.seta.model.attendance.SyncCheck;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * Created by riken on 4/8/14.
 */
@Repository
public class SqlSyncCheckDao extends SqlBaseDao implements SyncCheckDao {

    protected static final String SET_DATA_SQL =
            "INSERT INTO ts.sync_check(data_type, data_id, data_date, data_side)" +
            "VALUES(:dataType, :dataId, :dataDate, :dataSide)";

    @Override
    public boolean setSyncData(SyncCheck sc) {

        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("dataType", sc.getDataType());
        param.addValue("dataId", sc.getDataId());
        param.addValue("dataDate", sc.getDate());
        param.addValue("dataSide", sc.getDataSide());

        if(localNamedJdbc.update(SET_DATA_SQL, param)==1) return true;
        else return false;
    }
}

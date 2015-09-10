package gov.nysenate.seta.dao.personnel.mapper;

import gov.nysenate.seta.model.personnel.SupervisorOverride;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static gov.nysenate.seta.dao.base.SqlBaseDao.getLocalDate;
import static gov.nysenate.seta.dao.base.SqlBaseDao.getLocalDateTime;

public class SupervisorOverrideRowMapper implements RowMapper<SupervisorOverride>
{
    @Override
    public SupervisorOverride mapRow(ResultSet rs, int i) throws SQLException {
        SupervisorOverride supOvr = new SupervisorOverride();
        supOvr.setGranteeSupervisorId(rs.getInt("NUXREFEM"));
        supOvr.setGranterSupervisorId(rs.getInt("NUXREFSVSUB"));
        supOvr.setActive(rs.getString("CDSTATUS").equals("A"));
        supOvr.setOriginDate(getLocalDateTime(rs, "DTTXNORIGIN"));
        supOvr.setUpdateDate(getLocalDateTime(rs, "DTTXNUPDATE"));
        supOvr.setStartDate(Optional.ofNullable(getLocalDate(rs, "DTSTART")));
        supOvr.setEndDate(Optional.ofNullable(getLocalDate(rs, "DTEND")));
        return supOvr;
    }
}

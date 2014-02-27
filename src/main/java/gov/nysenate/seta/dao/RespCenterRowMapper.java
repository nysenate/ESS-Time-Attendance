package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.ResponsibilityCenter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RespCenterRowMapper implements RowMapper<ResponsibilityCenter>
{
    private String pfx;

    private RespHeadRowMapper respHeadMapper;
    private AgencyRowMapper agencyRowMapper;

    public RespCenterRowMapper(String pfx, String rctrHdPfx, String agcyPfx) {
        this.pfx = pfx;
        this.respHeadMapper = new RespHeadRowMapper(rctrHdPfx);
        this.agencyRowMapper = new AgencyRowMapper(agcyPfx);
    }

    @Override
    public ResponsibilityCenter mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResponsibilityCenter rctr = new ResponsibilityCenter();
        rctr.setCode(rs.getInt(pfx + "CDRESPCTR"));
        rctr.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        rctr.setName(rs.getString(pfx + "DERESPCTR"));
        rctr.setEffectiveDateBegin(rs.getDate(pfx + "DTEFFECTBEG"));
        rctr.setEffectiveDateEnd(rs.getDate(pfx + "DTEFFECTEND"));
        rctr.setHead(respHeadMapper.mapRow(rs, rowNum));
        rctr.setAgency(agencyRowMapper.mapRow(rs, rowNum));
        return rctr;
    }
}

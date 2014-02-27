package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.ResponsibilityHead;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RespHeadRowMapper implements RowMapper<ResponsibilityHead>
{
    private String pfx;

    public RespHeadRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public ResponsibilityHead mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResponsibilityHead rspHead = new ResponsibilityHead();
        rspHead.setCode(rs.getString(pfx + "CDRESPCTRHD"));
        rspHead.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        rspHead.setShortName(rs.getString(pfx + "DERESPCTRHDS"));
        rspHead.setName(rs.getString(pfx + "FFDERESPCTRHDF"));
        rspHead.setAffiliateCode(rs.getString(pfx + "CDAFFILIATE"));
        return rspHead;
    }
}

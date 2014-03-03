package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Location;
import gov.nysenate.seta.model.LocationType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationRowMapper implements RowMapper<Location>
{
    private String pfx;

    public LocationRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getString(pfx + "CDLOCAT") != null) {
            Location loc = new Location();
            loc.setCode(rs.getString(pfx + "CDLOCAT"));
            loc.setType(LocationType.valueOfCode(rs.getString(pfx + "CDLOCTYPE").charAt(0)));
            loc.setAddr1(rs.getString(pfx + "FFADSTREET1"));
            loc.setAddr2(rs.getString(pfx + "FFADSTREET2"));
            loc.setCity(rs.getString(pfx + "FFADCITY"));
            loc.setState(rs.getString(pfx + "ADSTATE"));
            loc.setZip5(rs.getString(pfx + "ADZIPCODE"));
            return loc;
        }
        return null;
    }
}

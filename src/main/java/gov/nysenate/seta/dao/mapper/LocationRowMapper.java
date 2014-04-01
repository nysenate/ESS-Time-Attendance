package gov.nysenate.seta.dao.mapper;

import gov.nysenate.seta.model.Address;
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
            Address addr = new Address();
            loc.setCode(rs.getString(pfx + "CDLOCAT"));
            loc.setType(LocationType.valueOfCode(rs.getString(pfx + "CDLOCTYPE").charAt(0)));
            addr.setAddr1(rs.getString(pfx + "FFADSTREET1"));
            addr.setAddr2(rs.getString(pfx + "FFADSTREET2"));
            addr.setCity(rs.getString(pfx + "FFADCITY"));
            addr.setState(rs.getString(pfx + "ADSTATE"));
            addr.setZip5(rs.getString(pfx + "ADZIPCODE"));
            loc.setAddress(addr);
            return loc;
        }
        return null;
    }
}

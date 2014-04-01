package gov.nysenate.seta.dao.mapper;

import gov.nysenate.seta.model.Address;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressRowMapper implements RowMapper<Address>
{
    private String pfx = "";

    public AddressRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getString(pfx + "ADSTREET1") != null) {
            Address address = new Address();
            address.setAddr1(rs.getString(pfx + "ADSTREET1"));
            address.setAddr2(rs.getString(pfx + "ADSTREET2"));
            address.setCity(rs.getString(pfx + "ADCITY"));
            address.setState(rs.getString(pfx + "ADSTATE"));
            address.setPostal(rs.getString(pfx + "ADZIPCODE"));
            return address;
        }
        return null;
    }
}
package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.Employee;
import gov.nysenate.seta.model.Gender;
import gov.nysenate.seta.model.MaritalStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRowMapper implements RowMapper<Employee>
{
    private String pfx = "";

    private RespCenterRowMapper respCenterRowMapper;
    private LocationRowMapper locationRowMapper;

    public EmployeeRowMapper(String pfx, String rctrPfx, String rctrhdPfx, String agcyPfx, String locPfx) {
        this.pfx = pfx;
        this.respCenterRowMapper = new RespCenterRowMapper(rctrPfx, rctrhdPfx, agcyPfx);
        this.locationRowMapper = new LocationRowMapper(locPfx);
    }

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        emp.setSupervisorId(rs.getInt(pfx +"NUXREFSV"));
        emp.setActive(rs.getString(pfx +"CDEMPSTATUS").equals("A"));
        emp.setFirstName(rs.getString(pfx +"FFNAFIRST"));
        emp.setInitial(rs.getString(pfx +"FFNAMIDINIT"));
        emp.setLastName(rs.getString(pfx +"FFNALAST"));
        emp.setFullName(rs.getString(pfx +"NAEMPLABEL"));
        emp.setTitle(rs.getString(pfx +"FFNATITLE"));
        emp.setSuffix(rs.getString(pfx +"FFNASUFFIX"));
        emp.setEmail(rs.getString(pfx +"NAEMAIL"));
        emp.setHomePhone(rs.getString(pfx +"ADPHONENUM"));
        emp.setWorkPhone(rs.getString(pfx +"ADPHONENUMW"));
        emp.setGender(Gender.valueOf(rs.getString(pfx +"CDSEX")));
        emp.setDateOfBirth(rs.getDate(pfx +"DTBIRTH"));
        emp.setMaritalStatus(MaritalStatus.valueOfCode(rs.getString(pfx +"CDMARITAL").charAt(0)));
        emp.setRespCenter(respCenterRowMapper.mapRow(rs, rowNum));
        emp.setLocation(locationRowMapper.mapRow(rs, rowNum));
        return emp;
    }
}

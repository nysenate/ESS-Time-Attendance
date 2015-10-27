package gov.nysenate.seta.dao.personnel.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.dao.payroll.mapper.RespCenterRowMapper;
import gov.nysenate.seta.dao.unit.AddressRowMapper;
import gov.nysenate.seta.dao.unit.LocationRowMapper;
import gov.nysenate.seta.model.payroll.PayType;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.model.personnel.Gender;
import gov.nysenate.seta.model.personnel.MaritalStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

import static gov.nysenate.seta.dao.base.SqlBaseDao.getLocalDate;

public class EmployeeRowMapper extends BaseRowMapper<Employee>
{
    private String pfx = "";

    private AddressRowMapper addressRowMapper;
    private RespCenterRowMapper respCenterRowMapper;
    private LocationRowMapper locationRowMapper;

    public EmployeeRowMapper(String pfx, String rctrPfx, String rctrhdPfx, String agcyPfx, String locPfx) {
        this.pfx = pfx;
        this.addressRowMapper = new AddressRowMapper(pfx);
        this.respCenterRowMapper = new RespCenterRowMapper(rctrPfx, rctrhdPfx, agcyPfx);
        this.locationRowMapper = new LocationRowMapper(locPfx);
    }

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        emp.setSupervisorId(rs.getInt(pfx + "NUXREFSV"));
        emp.setActive(rs.getString(pfx + "CDEMPSTATUS").equals("A"));
        emp.setFirstName(rs.getString(pfx + "FFNAFIRST"));
        emp.setInitial(rs.getString(pfx + "FFNAMIDINIT"));
        emp.setLastName(rs.getString(pfx + "FFNALAST"));
        emp.setFullName(rs.getString(pfx + "NAEMPLABEL"));
        emp.setTitle(rs.getString(pfx + "FFNATITLE"));
        emp.setSuffix(rs.getString(pfx + "FFNASUFFIX"));
        emp.setEmail(rs.getString(pfx + "NAEMAIL"));
        emp.setHomePhone(rs.getString(pfx + "ADPHONENUM"));
        emp.setWorkPhone(rs.getString(pfx + "ADPHONENUMW"));
        emp.setJobTitle(rs.getString(pfx + "FFDEEMPTITLL"));
        emp.setPayType(rs.getString(pfx + "CDPAYTYPE") != null ? PayType.valueOf(rs.getString(pfx + "CDPAYTYPE")) : null);
        emp.setGender(rs.getString(pfx + "CDSEX") != null ? Gender.valueOf(rs.getString(pfx + "CDSEX")) : null);
        emp.setDateOfBirth(getLocalDate(rs, pfx + "DTBIRTH"));
        emp.setMaritalStatus(rs.getString(pfx + "CDMARITAL") != null ? MaritalStatus.valueOfCode(rs.getString(pfx + "CDMARITAL")) : null);
        emp.setNid(rs.getString(pfx + "NUEMPLID"));
        emp.setHomeAddress(addressRowMapper.mapRow(rs, rowNum));
        emp.setRespCenter(respCenterRowMapper.mapRow(rs, rowNum));
        emp.setWorkLocation(locationRowMapper.mapRow(rs, rowNum));

        if (emp.getEmail() != null) {
            emp.setUid(emp.getEmail().split("@")[0]);
        }
        return emp;
    }
}
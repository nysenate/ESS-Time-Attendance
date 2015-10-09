package gov.nysenate.seta.dao.payroll.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.model.payroll.Deduction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeductionRowMapper extends BaseRowMapper<Deduction>
{

    @Override
    public Deduction mapRow(ResultSet rs, int i) throws SQLException {
        Deduction deduction = new Deduction(
                rs.getString("CDDEDUCTION"), rs.getString("DEDEDUCTIONF"), rs.getBigDecimal("MODEDUCTION"));
        return deduction;
    }
}

package gov.nysenate.seta.dao.accrual.mapper;

import gov.nysenate.seta.dao.period.PayPeriodRowMapper;
import gov.nysenate.seta.model.accrual.PeriodAccrualUsage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PeriodAccrualUsageRowMapper implements RowMapper<PeriodAccrualUsage>
{
    protected String pfx = "";
    protected PayPeriodRowMapper payPeriodRowMapper;

    public PeriodAccrualUsageRowMapper(String pfx, String payPeriodPfx) {
        this.pfx = pfx;
        this.payPeriodRowMapper = new PayPeriodRowMapper(payPeriodPfx);
    }

    @Override
    public PeriodAccrualUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
        PeriodAccrualUsage accUsage = new PeriodAccrualUsage();
        accUsage.setYear(rs.getInt(pfx + "YEAR"));
        accUsage.setPayPeriod(payPeriodRowMapper.mapRow(rs, rowNum));
        AccrualUsageRowMapper.mapRow(rs, accUsage);
        return accUsage;
    }
}

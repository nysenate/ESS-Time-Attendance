package gov.nysenate.seta.dao.accrual.mapper;

import gov.nysenate.seta.dao.period.PayPeriodRowMapper;
import gov.nysenate.seta.model.accrual.PeriodAccrualSummary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PeriodAccrualSummaryRowMapper implements RowMapper<PeriodAccrualSummary>
{
    protected String pfx = "";
    protected PayPeriodRowMapper payPeriodRowMapper;

    public PeriodAccrualSummaryRowMapper(String pfx, String payPeriodPfx) {
        this.pfx = pfx;
        this.payPeriodRowMapper = new PayPeriodRowMapper(payPeriodPfx);
    }

    @Override
    public PeriodAccrualSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        PeriodAccrualSummary perAccSum = new PeriodAccrualSummary();
        perAccSum.setYear(rs.getInt(pfx + "YEAR"));
        perAccSum.setPrevTotalHours(rs.getBigDecimal(pfx + "PREV_TOTAL_HRS"));
        perAccSum.setExpectedTotalHours(rs.getBigDecimal(pfx + "EXPECTED_TOTAL_HRS"));
        perAccSum.setExpectedBiweekHours(rs.getBigDecimal(pfx + "EXPECTED_BIWEEK_HRS"));
        perAccSum.setSickRate(rs.getBigDecimal(pfx + "SICK_RATE"));
        perAccSum.setVacRate(rs.getBigDecimal(pfx + "VAC_RATE"));
        perAccSum.setBasePayPeriod(payPeriodRowMapper.mapRow(rs, rowNum));
        AccrualSummaryRowMapper.mapRow(rs, perAccSum);
        return perAccSum;
    }
}

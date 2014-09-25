package gov.nysenate.seta.dao.accrual.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.dao.period.mapper.PayPeriodRowMapper;
import gov.nysenate.seta.model.accrual.PeriodAccSummary;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PeriodAccSummaryRowMapper extends BaseRowMapper<PeriodAccSummary>
{
    protected String pfx = "";
    protected PayPeriodRowMapper payPeriodRowMapper;

    public PeriodAccSummaryRowMapper(String pfx, String payPeriodPfx) {
        this.pfx = pfx;
        this.payPeriodRowMapper = new PayPeriodRowMapper(payPeriodPfx);
    }

    @Override
    public PeriodAccSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        PeriodAccSummary perAccSum = new PeriodAccSummary();
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

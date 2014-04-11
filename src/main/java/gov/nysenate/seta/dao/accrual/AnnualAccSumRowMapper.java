package gov.nysenate.seta.dao.accrual;

import gov.nysenate.seta.model.accrual.AnnualAccrualSummary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnnualAccSumRowMapper implements RowMapper<AnnualAccrualSummary>
{
    @Override
    public AnnualAccrualSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        AnnualAccrualSummary annAccRec = new AnnualAccrualSummary();
        annAccRec.setYear(rs.getInt("YEAR"));
        annAccRec.setCloseDate(rs.getDate("CLOSE_DATE"));
        annAccRec.setEndDate(rs.getDate("DTEND"));
        annAccRec.setPayPeriodsYtd(rs.getInt("PAY_PERIODS_YTD"));
        annAccRec.setPayPeriodsBanked(rs.getInt("PAY_PERIODS_BANKED"));
        AccSummaryRowMapper.mapRow(rs, annAccRec);
        return annAccRec;
    }
}

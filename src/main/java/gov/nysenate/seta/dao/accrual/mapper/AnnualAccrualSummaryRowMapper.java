package gov.nysenate.seta.dao.accrual.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.model.accrual.AnnualAccrualSummary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnnualAccrualSummaryRowMapper extends BaseRowMapper<AnnualAccrualSummary>
{
    @Override
    public AnnualAccrualSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        AnnualAccrualSummary annAccRec = new AnnualAccrualSummary();
        annAccRec.setYear(rs.getInt("YEAR"));
        annAccRec.setCloseDate(rs.getDate("CLOSE_DATE"));
        annAccRec.setEndDate(rs.getDate("DTEND"));
        annAccRec.setContServiceDate(rs.getDate("CONT_SERVICE_DATE"));
        annAccRec.setPayPeriodsYtd(rs.getInt("PAY_PERIODS_YTD"));
        annAccRec.setPayPeriodsBanked(rs.getInt("PAY_PERIODS_BANKED"));
        AccrualSummaryRowMapper.mapRow(rs, annAccRec);
        return annAccRec;
    }
}

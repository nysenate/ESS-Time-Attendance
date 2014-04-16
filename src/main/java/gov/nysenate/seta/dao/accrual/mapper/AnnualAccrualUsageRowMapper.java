package gov.nysenate.seta.dao.accrual.mapper;

import gov.nysenate.seta.model.accrual.AnnualAccrualUsage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnnualAccrualUsageRowMapper implements RowMapper<AnnualAccrualUsage>
{
    @Override
    public AnnualAccrualUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
        AnnualAccrualUsage annAccUsage = new AnnualAccrualUsage();
        annAccUsage.setLatestStartDate(rs.getDate("LATEST_DTBEGIN"));
        annAccUsage.setLatestEndDate(rs.getDate("LATEST_DTEND"));
        AccrualUsageRowMapper.mapRow(rs, annAccUsage);
        return annAccUsage;
    }
}

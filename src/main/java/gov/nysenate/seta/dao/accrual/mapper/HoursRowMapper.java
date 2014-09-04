package gov.nysenate.seta.dao.accrual.mapper;


import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.dao.period.mapper.PayPeriodRowMapper;
import gov.nysenate.seta.model.accrual.AccrualSummary;
import gov.nysenate.seta.model.accrual.Hours;
import gov.nysenate.seta.model.accrual.PeriodAccrualUsage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of AccrualSummary.
 */
public class HoursRowMapper extends BaseRowMapper<Hours>
{
    @Override
    public Hours mapRow(ResultSet rs, int rowNum) throws SQLException {
        Hours hours = new Hours();
        hours.setEndDate(rs.getDate(1));
        hours.setHours(rs.getBigDecimal(2));
        return hours;
    }
}
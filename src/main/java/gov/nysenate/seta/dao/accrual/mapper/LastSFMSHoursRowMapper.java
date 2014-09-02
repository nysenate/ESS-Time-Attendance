package gov.nysenate.seta.dao.accrual.mapper;


import gov.nysenate.seta.model.accrual.Hours;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of AccrualSummary.
 */
public class LastSFMSHoursRowMapper implements RowMapper<Hours>
{
    @Override
    public Hours mapRow(ResultSet rs, int rowNum) throws SQLException {
        Hours hours = new Hours();
        BigDecimal totalHours = rs.getBigDecimal(2);
        totalHours = totalHours.add(rs.getBigDecimal(3));
        hours.setEndDate(rs.getDate(1));
        hours.setHours(totalHours);

        return hours;
    }
}
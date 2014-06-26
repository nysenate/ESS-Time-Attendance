package gov.nysenate.seta.dao.allowances.mapper;


import gov.nysenate.seta.dao.accrual.mapper.AccrualUsageRowMapper;
import gov.nysenate.seta.model.accrual.AccrualSummary;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of Allowance.
 */
public class AllowanceRowMapper
{
    /**
     * Sets accrual summary columns on the supplied AccrualSummary object.
     * @throws java.sql.SQLException
     */
    public static void mapRow(ResultSet rs, AccrualSummary accSummary) throws SQLException {
        if (accSummary == null) {
            throw new IllegalArgumentException("Accrual summary cannot be null!");
        }
        AllowanceRowMapper.mapRow(rs, accSummary);
      /*  accSummary.setVacHoursAccrued(rs.getBigDecimal("VAC_HRS_ACCRUED"));
        accSummary.setVacHoursBanked(rs.getBigDecimal("VAC_HRS_BANKED"));
        accSummary.setPerHoursAccrued(rs.getBigDecimal("PER_HRS_ACCRUED"));
        accSummary.setEmpHoursAccrued(rs.getBigDecimal("EMP_HRS_ACCRUED"));
        accSummary.setEmpHoursBanked(rs.getBigDecimal("EMP_HRS_BANKED"));
        accSummary.setMiscHoursUsed(rs.getBigDecimal("MISC_HRS_USED"));*/
    }
}

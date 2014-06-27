package gov.nysenate.seta.dao.allowances.mapper;

import gov.nysenate.seta.model.allowances.AllowanceUsage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of Allowance.
 */
public class AllowanceRowMapper  implements RowMapper<AllowanceUsage>
{
    protected String pfx;

    public AllowanceRowMapper(String pfx) {
        this.pfx = pfx;
    }
    /**
     * Sets accrual summary columns on the supplied AccrualSummary object.
     * @throws java.sql.SQLException
     */

    public  AllowanceUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
        AllowanceUsage allowanceUsage = new AllowanceUsage();
        Date endDate = rs.getDate(pfx + "DTENDTE");
        int dtyear = new Integer(new SimpleDateFormat("yyyy").format(endDate)).intValue();
        allowanceUsage.setMoneyUsed(rs.getBigDecimal(pfx + "TE_AMOUNT_PAID"));
        allowanceUsage.setHoursUsed(rs.getBigDecimal(pfx + "TE_HRS_PAID"));
        allowanceUsage.setEndDate(endDate);
        allowanceUsage.setYear(dtyear);
        return allowanceUsage;
    }
}

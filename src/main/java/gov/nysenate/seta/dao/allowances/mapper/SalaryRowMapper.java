package gov.nysenate.seta.dao.allowances.mapper;

import gov.nysenate.seta.model.allowances.AllowanceUsage;
import gov.nysenate.seta.model.payroll.SalaryRec;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of Allowance.
 */
public class SalaryRowMapper implements RowMapper<SalaryRec>
{
    protected String pfx;

    public SalaryRowMapper(String pfx) {
        this.pfx = pfx;
    }
    /**
     * Sets accrual summary columns on the supplied AccrualSummary object.
     * @throws java.sql.SQLException
     */

    public SalaryRec mapRow(ResultSet rs, int rowNum) throws SQLException {
        SalaryRec salaryRec = new SalaryRec();
        salaryRec.setSalary(rs.getBigDecimal(pfx + "mosalbiwkly"));
        salaryRec.setEffectDate(rs.getDate(pfx + "dteffect"));
        return salaryRec;
    }
}

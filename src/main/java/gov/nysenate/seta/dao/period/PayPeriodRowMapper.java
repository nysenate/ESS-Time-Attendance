package gov.nysenate.seta.dao.period;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PayPeriodRowMapper extends BaseRowMapper<PayPeriod>
{
    protected String pfx;

    public PayPeriodRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public PayPeriod mapRow(ResultSet rs, int rowNum) throws SQLException {
        PayPeriod period = new PayPeriod();
        period.setActive(rs.getString("CDSTATUS").equals("A"));
        period.setType(PayPeriodType.valueOf(rs.getString("CDPERIOD")));
        period.setPayPeriodNum(rs.getInt("NUPERIOD"));
        period.setStartDate(rs.getDate("DTBEGIN"));
        period.setEndDate(rs.getDate("DTEND"));
        return period;
    }
}

package gov.nysenate.seta.dao.period.mapper;

import gov.nysenate.seta.dao.base.BaseRowMapper;
import gov.nysenate.seta.model.period.PayPeriod;
import gov.nysenate.seta.model.period.PayPeriodType;

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
        period.setActive(getStatusFromCode(rs.getString(pfx + "CDSTATUS")));
        period.setType(PayPeriodType.valueOf(rs.getString(pfx + "CDPERIOD")));
        period.setPayPeriodNum(rs.getInt(pfx + "NUPERIOD"));
        period.setStartDate(getLocalDateFromRs(rs, pfx + "DTBEGIN"));
        period.setEndDate(getLocalDateFromRs(rs, pfx + "DTEND"));
        return period;
    }
}

package gov.nysenate.seta.dao.payroll;

import gov.nysenate.seta.model.payroll.Holiday;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HolidayRowMapper implements RowMapper<Holiday>
{
    protected String pfx;

    public HolidayRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Holiday mapRow(ResultSet rs, int rowNum) throws SQLException {
        Holiday holiday = new Holiday();
        holiday.setActive(rs.getString("CDSTATUS").equals("A"));
        holiday.setDate(rs.getDate("DTHOLIDAY"));
        holiday.setName(rs.getString("DEHOLIDAY"));
        holiday.setQuestionable(rs.getString("CDQUEST").equals("Y"));
        return holiday;
    }
}

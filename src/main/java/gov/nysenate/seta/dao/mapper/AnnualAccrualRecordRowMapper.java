package gov.nysenate.seta.dao.mapper;

import gov.nysenate.seta.dao.AnnualAccrualRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnnualAccrualRecordRowMapper implements RowMapper<AnnualAccrualRecord>
{
    @Override
    public AnnualAccrualRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        AnnualAccrualRecord annAccRec = new AnnualAccrualRecord();
        annAccRec.setYear(rs.getInt("YEAR"));
        annAccRec.setCloseDate(rs.getDate("CLOSE_DATE"));
        annAccRec.setEndDate(rs.getDate("DTEND"));
        annAccRec.setVacHoursAccrued(rs.getBigDecimal("VAC_HRS_ACCRUED"));
        annAccRec.setVacHoursUsed(rs.getBigDecimal("VAC_HRS_USED"));
        annAccRec.setVacHoursBanked(rs.getBigDecimal("VAC_HRS_BANKED"));
        annAccRec.setPerHoursAccrued(rs.getBigDecimal("PER_HRS_ACCRUED"));
        annAccRec.setPerHoursUsed(rs.getBigDecimal("PER_HRS_USED"));
        annAccRec.setFamHoursUsed(rs.getBigDecimal("FAM_HRS_USED"));
        annAccRec.setEmpHoursAccrued(rs.getBigDecimal("EMP_HRS_ACCRUED"));
        annAccRec.setEmpHoursUsed(rs.getBigDecimal("EMP_HRS_USED"));
        annAccRec.setEmpHoursBanked(rs.getBigDecimal("EMP_HRS_BANKED"));
        annAccRec.setHolHoursUsed(rs.getBigDecimal("HOL_HRS_USED"));
        annAccRec.setMiscHoursUsed(rs.getBigDecimal("MISC_HRS_USED"));
        annAccRec.setWorkHoursTotal(rs.getBigDecimal("WORK_HRS_TOTAL"));
        annAccRec.setTravelHoursTotal(rs.getBigDecimal("TRV_HRS_TOTAL"));
        annAccRec.setPayPeriodsYtd(rs.getInt("PAY_PERIODS_YTD"));
        annAccRec.setPayPeriodsBanked(rs.getInt("PAY_PERIODS_BANKED"));
        return annAccRec;
    }
}

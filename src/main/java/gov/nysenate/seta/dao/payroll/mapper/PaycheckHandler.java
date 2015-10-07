package gov.nysenate.seta.dao.payroll.mapper;

import gov.nysenate.seta.dao.base.BaseHandler;
import gov.nysenate.seta.dao.payroll.DeductionDao;
import gov.nysenate.seta.model.payroll.Deduction;
import gov.nysenate.seta.model.payroll.Paycheck;
import gov.nysenate.seta.model.personnel.Employee;
import gov.nysenate.seta.service.personnel.EssCachedEmployeeInfoService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaycheckHandler extends BaseHandler
{
    private DeductionDao deductionDao;
    private int empId;
    private List<Paycheck> paychecks;

    public PaycheckHandler(int empId, DeductionDao deductionDao) {
        this.deductionDao = deductionDao;
        this.empId = empId;
        paychecks = new ArrayList<>();
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        List<Deduction> deductions = deductionDao.getDeductionsForPaycheck(empId, getLocalDateFromRs(rs, "DTCHECK"));
        Paycheck paycheck = new Paycheck(rs.getBigDecimal("MOANNUALSAL"), rs.getString("NAEMPPAYSR"), rs.getString("DEEMPTITLE"),
                                         rs.getString("NUPERIOD"), getLocalDateFromRs(rs, "DTCHECK"), rs.getString("CDAGENCY"),
                                         rs.getString("NULINE"), rs.getBigDecimal("MOGROSS"), rs.getBigDecimal("MONET"),
                                         deductions, rs.getBigDecimal("MOADVICEAMT"), rs.getBigDecimal("MOCHECKAMT"));
        paychecks.add(paycheck);
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public List<Paycheck> getPaychecks() {
        return paychecks;
    }
}

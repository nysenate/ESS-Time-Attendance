package gov.nysenate.seta.service.base;

import gov.nysenate.seta.dao.accrual.SqlAccrualDao;
import gov.nysenate.seta.dao.attendance.SqlTimeRecordDao;
import gov.nysenate.seta.dao.period.SqlPayPeriodDao;
import gov.nysenate.seta.dao.personnel.SqlEmployeeDao;
import gov.nysenate.seta.dao.personnel.SqlSupervisorDao;
import gov.nysenate.seta.dao.transaction.SqlEmpTransactionDao;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SqlDaoBackedService
{
    @Autowired protected SqlEmpTransactionDao empTransactionDao;
    @Autowired protected SqlPayPeriodDao payPeriodDao;
    @Autowired protected SqlTimeRecordDao timeRecordDao;
    @Autowired protected SqlSupervisorDao supervisorDao;
    @Autowired protected SqlEmployeeDao employeeDao;
    @Autowired protected SqlAccrualDao accrualDao;
}
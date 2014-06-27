package gov.nysenate.seta.dao.allowances;

/**
 * Created by heitner on 6/25/2014.
 */

import gov.nysenate.seta.dao.base.BaseDao;
import gov.nysenate.seta.model.allowances.AllowanceUsage;

import java.util.LinkedList;
import java.util.List;

/**
 * Data access layer for retrieving and computing allowance information
 * (e.g temporary employee yearly allowances, hours used).
 */
public interface AllowanceDao extends BaseDao
{
    LinkedList<AllowanceUsage> getAllowanceUsage(int empId, int year);
    }
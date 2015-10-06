package gov.nysenate.seta.model.payroll;

import java.math.BigDecimal;

/**
 * Any deduction that can be applied to a pay check.
 * e.g. Federal taxes, Health insurance, etc.
 */
public class Deduction
{
    private int id;
    private String description;
    private BigDecimal amount;

}

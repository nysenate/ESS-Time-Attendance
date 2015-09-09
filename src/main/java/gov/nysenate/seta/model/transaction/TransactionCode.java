package gov.nysenate.seta.model.transaction;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.nysenate.seta.model.transaction.TransactionType.PAY;
import static gov.nysenate.seta.model.transaction.TransactionType.PER;

/**
 * The TransactionCode refers to a specific change made to an employee's record.
 * The dbColumns stored for each code refer to the set of columns in the SFMS audit
 * table where the relevant changes are stored.
 */
public enum TransactionCode
{
    ACC(PER, "CDACCRUE", "ACCRUE"),
    ACT(PER, "DTAPPOINTFRM, DTAPPOINTTO", "ACCRUAL DATES"),
    ADT(PER, "DTPEREMPFRM, DTPEREMPTO", "EMPLOYMENT DATES"),
    AGY(PAY, "CDAGENCY", "AGENCY CODE"),
    ALL(PAY, "", "ACTIVE NON-TEMP EMPLOYEES"),
    APP(PER, "", "APPOINTMENT"),
    CAT(PAY, "MOADDCITYTAX", "ADDITIONAL NYC WITHHELD"),
    CHK(PAY, "CDCERTADD, ADSTREET1C, ADSTREET2C, ADSTREET3C, ADSTATEC, ADCITYC, ADZIPCODEC", "CHECK MAILING ADDRESS"),
    CMS(PAY, "CDMARITALNYC", "NYC MARITAL STATUS"),
    CNU(PER, "CDNEGUNIT", "NEGOTIATING UNIT"),
    CUA(PAY, "MOCREDUN", "CREDIT UNION AMOUNT"),
    CUC(PAY, "CDCREDUN", "CREDIT UNION CODE"),
    CWT(PAY, "NUCITYTAXEX", "NYC WITHHOLDING EXEMP"),
    DDF(PAY, "CDDIRECTDEPF", "DIRECT DEPOSIT FULL"),
    DDP(PAY, "CDDIRECTDEPP", "DIRECT DEPOSIT PARTIAL"),
    DEF(PER, "CDDEFLAG, DTDEFLAG", "DEFERRED LAG"),
    DIS(PAY, "CDCHKDIST", "CHECK DISTRIBUTION CODE"),
    DOB(PER, "DTBIRTH", "DATE OF BIRTH"),
    DUA(PER, "CDDUALEMP", "DUAL EMPLOYEE"),
    EEV(PER, "DTI9REC", "I9 RECEIVED DATE"),
    EMP(PER, "CDEMPSTATUS", "TERMINATION"),
    ENC(PAY, "DTLEAVEEND", "ENCUMBERING LEAVE"),
    EXC(PAY, "MOAMTEXCEED", "NOT TO EXCEED AMOUNT"),
    FAT(PAY, "MOADDFEDTAX", "ADDITION FEDERAL WITHHELD"),
    FMS(PAY, "CDMARITALFED", "FEDERAL MARITAL STATUS"),
    FWT(PAY, "NUFEDTAXEX", "FEDERAL WITHHOLDING EXEMP"),
    HWT(PAY, "DTENDTE, NUHRHRSPD, MOHRHRSPD, DEHRDAYSPD", "HOURLY WORK PAID"),
    IDC(PER, "CDIDCARD", "ID CARD"),
    LEG(PER, "ADSTREET1, ADSTREET2, ADCITY, ADSTATE, ADZIPCODE, ADCOUNTY", "LEGAL ADDRESS"),
    LIM(PER, "DELIMITATION", "EMPLOYMENT LIMITATION"),
    LIN(PAY, "NULINE", "LINE NUMBER"),
    LOC(PER, "CDLOCAT", "WORK LOCATION"),
    LTD(PAY, "NUDAYSLOST, MOLOSTTIME", "LOST TIME DAYS"),
    MAR(PER, "CDMARITAL", "MARITAL STATUS"),
    MIN(PER, "NUMINTOTEND, NUMINTOTHRS", "MINIMUM TOTAL HOURS"),
    NAM(PER, "NALAST, NAFIRST, NAMIDINIT", "NAME"),
    NAT(PER, "NATITLE", "NAME"),
    OED(PER, "DTORGEMPLOY", "ORIGINAL EMPLOYMENT DATE"),
    PHO(PER, "ADPHONENUM, ADPHONENUMW", "PHONE"),
    PLY(PAY, "", "EMPLOYER"),
    POS(PAY, "NUPOSITION", "POSITION NUM"),
    PRA(PER, "DTAGENCYTERM, CDAGENCYPR, DTTRANMEMREC", "PREVIOUS AGENCY"),
    PRS(PER, "", "PRESS LIST"),
    PYA(PAY, "MOPRIORYRTE", "TE PRIOR YEAR AMOUNT"),
    RHD(PER, "DTCONTSERV", "REHIRE DATE"),
    RSH(PAY, "", "RESP. CENTER HEAD CODE"),   /** TODO: Fix this.. */
    RTP(PER, "", "RE-APPOINTMENT"),
    SAL(PAY, "MOSALBIWKLY", "BIWEEKLY/HOURLY RATE"),
    SAT(PAY, "MOADDSTATTAX", "ADDITION STATE WITHHELD"),
    SEX(PER, "CDSEX", "SEX CODE"),
    SMS(PAY, "CDMARITALST", "STATE MARITAL STATUS"),
    SPE(PER, "CDSTATPER", "PERSONNEL STATUS"),
    SUP(PER, "NAFIRSTSUP, NALASTSUP, NUXREFSV", "ATTENDANCE SUPERVISOR"),
    SWT(PAY, "NUSTATTAXEX", "STATE WITHHOLDING EXEMP"),
    TAX(PAY, "", "TAX"),
    THP(PAY, "MOTOTHRSPD", "TOTAL HOURS PAID"),
    TLE(PER, "CDEMPTITLE", "EMPLOYEE TITLE CODE"),
    TLT(PAY, "DETIMELOST, DEDAYSLOST", "LOST TIME"),
    TXN(PAY, "DETXNNOTEPAY", "PAYROLL TRANSACTION NOTE"),
    TYP(PAY, "CDPAYTYPE", "PAYROLL TYPE"),
    VHL(PER, "NUVACHRSLF", "VACATION HRS PD LIFE TIME"),
    VHP(PER, "NUVACHRSPD", "VACATION HOURS PAID"),
    VMP(PAY, "MOVACHRSPD", "VACATION MONEY PAID"),
    WDT(PER, "DTW4SIGNED", "W4 SIGN DATE"),
    YAT(PAY, "MOADDYONTAX", "ADDITIONAL YON WITHHELD"),
    YMS(PAY, "CDMARITALYON", "YONKERS MARITAL STATUS"),
    YWT(PAY, "NUYONTAXEX", "YONKERS WITHHOLDING EXEMP");

    /** The type of transaction, e.g. Payroll, Personnel */
    private TransactionType type;

    /** The SFMS database columns that this transaction affects. */
    private String dbColumns;

    /** Description of the transaction. */
    private String desc;

    /** --- Constructors --- */

    private TransactionCode(TransactionType type, String dbColumns, String desc) {
        this.type = type;
        this.dbColumns = dbColumns;
        this.desc = desc;
    }

    public static Set<TransactionCode> getAll() {
        return EnumSet.allOf(TransactionCode.class);
    }

    /** --- Methods --- */

    public boolean isAppointType() {
        return this.equals(APP) || this.equals(RTP);
    }

    public boolean usesAllColumns() {
        return this.isAppointType();
    }

    /** --- Functional Getters/Setters --- */

    public List<String> getDbColumnList() {
        return (this.usesAllColumns())
            ? getAllDbColumnsList()
            : Arrays.asList(StringUtils.split(dbColumns, ","));
    }

    public static List<String> getAllDbColumnsList() {
        List<String> columns = new ArrayList<>();
        for (TransactionCode t : TransactionCode.values()) {
            if (!t.usesAllColumns()) {
                columns.addAll(t.getDbColumnList());
            }
        }
        return columns;
    }

    /** --- Basic Getters/Setters --- */

    public TransactionType getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
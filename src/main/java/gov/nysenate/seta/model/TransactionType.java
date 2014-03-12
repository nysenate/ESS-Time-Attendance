package gov.nysenate.seta.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The TransactionType refers to a specific change made to an employee's personnel record.
 * The dbColumns stored for each type refer to the set of columns in the SFMS audit table
 * where the relevant changes are stored.
 */
public enum TransactionType
{
    ACC("PER", "CDACCRUE", "ACCRUE"),
    ACT("PER", "DTAPPOINTFRM, DTAPPOINTTO", "ACCRUAL DATES"),
    ADT("PER", "DTPEREMPFRM, DTPEREMPTO", "EMPLOYMENT DATES"),
    AGY("PAY", "CDAGENCY", "AGENCY CODE"),
    ALL("PAY", "", "ACTIVE NON-TEMP EMPLOYEES"),
    APP("PER", "", "APPOINTMENT"),
    CAT("PAY", "MOADDCITYTAX", "ADDITIONAL NYC WITHHELD"),
    CHK("PAY", "CDCERTADD, ADSTREET1C, ADSTREET2C, ADSTREET3C, ADSTATEC, ADCITYC, ADZIPCODEC", "CHECK MAILING ADDRESS"),
    CMS("PAY", "CDMARITALNYC", "NYC MARITAL STATUS"),
    CNU("PER", "CDNEGUNIT", "NEGOTIATING UNIT"),
    CUA("PAY", "MOCREDUN", "CREDIT UNION AMOUNT"),
    CUC("PAY", "CDCREDUN", "CREDIT UNION CODE"),
    CWT("PAY", "NUCITYTAXEX", "NYC WITHHOLDING EXEMP"),
    DDF("PAY", "CDDIRECTDEPF", "DIRECT DEPOSIT FULL"),
    DDP("PAY", "CDDIRECTDEPP", "DIRECT DEPOSIT PARTIAL"),
    DEF("PER", "CDDEFLAG, DTDEFLAG", "DEFERRED LAG"),
    DIS("PAY", "CDCHKDIST", "CHECK DISTRIBUTION CODE"),
    DOB("PER", "DTBIRTH", "DATE OF BIRTH"),
    DUA("PER", "CDDUALEMP", "DUAL EMPLOYEE"),
    EEV("PER", "DTI9REC", "I9 RECEIVED DATE"),
    EMP("PER", "CDEMPSTATUS", "TERMINATION"),
    ENC("PAY", "DTLEAVEEND", "ENCUMBERING LEAVE"),
    EXC("PAY", "MOAMTEXCEED", "NOT TO EXCEED AMOUNT"),
    FAT("PAY", "MOADDFEDTAX", "ADDITION FEDERAL WITHHELD"),
    FMS("PAY", "CDMARITALFED", "FEDERAL MARITAL STATUS"),
    FWT("PAY", "NUFEDTAXEX", "FEDERAL WITHHOLDING EXEMP"),
    HWT("PAY", "DTENDTE, NUHRHRSPD, MOHRHRSPD, DEHRDAYSPD", "HOURLY WORK PAID"),
    IDC("PER", "CDIDCARD", "ID CARD"),
    LEG("PER", "ADSTREET1, ADSTREET2, ADCITY, ADSTATE, ADZIPCODE, ADCOUNTY", "LEGAL ADDRESS"),
    LIM("PER", "DELIMITATION", "EMPLOYMENT LIMITATION"),
    LIN("PAY", "NULINE", "LINE NUMBER"),
    LOC("PER", "CDLOCAT", "WORK LOCATION"),
    LTD("PAY", "NUDAYSLOST, MOLOSTTIME", "LOST TIME DAYS"),
    MAR("PER", "CDMARITAL", "MARITAL STATUS"),
    MIN("PER", "NUMINTOTEND, NUMINTOTHRS", "MINIMUM TOTAL HOURS"),
    NAM("PER", "NALAST, NAFIRST, NAMIDINIT", "NAME"),
    NAT("PER", "NATITLE", "NAME"),
    OED("PER", "DTORGEMPLOY", "ORIGINAL EMPLOYMENT DATE"),
    PHO("PER", "ADPHONENUM, ADPHONENUMW", "PHONE"),
    PLY("PAY", "", "EMPLOYER"),
    POS("PAY", "NUPOSITION", "POSITION NUM"),
    PRA("PER", "DTAGENCYTERM, CDAGENCYPR, DTTRANMEMREC", "PREVIOUS AGENCY"),
    PRS("PER", "", "PRESS LIST"),
    PYA("PAY", "MOPRIORYRTE", "TE PRIOR YEAR AMOUNT"),
    RHD("PER", "DTCONTSERV", "REHIRE DATE"),
    RSH("PAY", "", "RESP. CENTER HEAD CODE"),   /** TODO: Fix this.. */
    RTP("PER", "", "RE-APPOINTMENT"),
    SAL("PAY", "MOSALBIWKLY", "BIWEEKLY/HOURLY RATE"),
    SAT("PAY", "MOADDSTATTAX", "ADDITION STATE WITHHELD"),
    SEX("PER", "CDSEX", "SEX CODE"),
    SMS("PAY", "CDMARITALST", "STATE MARITAL STATUS"),
    SPE("PER", "CDSTATPER", "PERSONNEL STATUS"),
    SUP("PER", "NAFIRSTSUP, NALASTSUP, NUXREFSV", "ATTENDANCE SUPERVISOR"),
    SWT("PAY", "NUSTATTAXEX", "STATE WITHHOLDING EXEMP"),
    TAX("PAY", "", "TAX"),
    THP("PAY", "MOTOTHRSPD", "TOTAL HOURLY PAY"),
    TLE("PER", "CDEMPTITLE", "EMPLOYEE TITLE CODE"),
    TLT("PAY", "DETIMELOST, DEDAYSLOST", "LOST TIME"),
    TXN("PAY", "DETXNNOTEPAY", "PAYROLL TRANSACTION NOTE"),
    TYP("PAY", "CDPAYTYPE", "PAYROLL TYPE"),
    VHL("PER", "NUVACHRSLF", "VACATION HRS PD LIFE TIME"),
    VHP("PER", "NUVACHRSPD", "VACATION HOURS PAID"),
    VMP("PAY", "MOVACHRSPD", "VACATION MONEY PAID"),
    WDT("PER", "DTW4SIGNED", "W4 SIGN DATE"),
    YAT("PAY", "MOADDYONTAX", "ADDITIONAL YON WITHHELD"),
    YMS("PAY", "CDMARITALYON", "YONKERS MARITAL STATUS"),
    YWT("PAY", "NUYONTAXEX", "YONKERS WITHHOLDING EXEMP");

    private TransactionType(String type, String dbColumns, String desc) {
        this.type = type;
        this.dbColumns = dbColumns;
        this.desc = desc;
    }

    private String type;
    private String dbColumns;
    private String desc;

    public String getType() {
        return type;
    }

    public String getDbColumns() {
        return dbColumns;
    }

    public List<String> getDbColumnList() {
        return Arrays.asList(StringUtils.split(dbColumns, ","));
    }

    public static List<String> getAllDbColumnsList() {
        List<String> columns = new ArrayList<>();
        for (TransactionType t : TransactionType.values()) {
            columns.addAll(t.getDbColumnList());
        }
        return columns;
    }

    public String getDesc() {
        return desc;
    }
}

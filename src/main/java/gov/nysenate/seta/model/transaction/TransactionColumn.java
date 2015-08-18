package gov.nysenate.seta.model.transaction;

public enum TransactionColumn
{
    ADCITY("City"),
    ADCITYC("City Code"),
    ADCOUNTY("County"),
    ADPHONENUM("Phone Number"),
    ADPHONENUMW("Work Phone Number"),
    ADSTATE("State"),
    ADSTATEC("State Code"),
    ADSTREET1("Address Line 1"),
    ADSTREET1C(""),
    ADSTREET2("Address Line 2"),
    ADSTREET2C(""),
    ADSTREET3C("Adress Line 3"),
    ADZIPCODE("Zip Code"),
    ADZIPCODEC(""),
    CDACCRUE("Accruals active"),
    CDAGENCY("Agency Code"),
    CDAGENCYPR(""),
    CDCERTADD(""),
    CDCHKDIST(""),
    CDCREDUN(""),
    CDDEFLAG(""),
    CDDIRECTDEPF(""),
    CDDIRECTDEPP(""),
    CDDUALEMP("Dual Employment Status"),
    CDEMPSTATUS("Employment Status"),
    CDEMPTITLE("Employee Title"),
    CDIDCARD(""),
    CDLOCAT("Location Code"),
    CDMARITAL("Marital Status"),
    CDMARITALFED(""),
    CDMARITALNYC(""),
    CDMARITALST(""),
    CDMARITALYON(""),
    CDNEGUNIT("Negotiation Unit"),
    CDPAYTYPE("Pay Type"),
    CDSEX("Sex"),
    CDSTATPER(""),
    DEDAYSLOST(""),
    DEHRDAYSPD(""),
    DELIMITATION("Limitation"),
    DETIMELOST(""),
    DETXNNOTEPAY("Payroll Notice"),
    DTAGENCYTERM("Date Terminated from Agency"),
    DTAPPOINTFRM("Appointed From"),
    DTAPPOINTTO("Appointed To"),
    DTBIRTH("Date of Birth"),
    DTCONTSERV("Continuous Service Since"),
    DTDEFLAG(""),
    DTENDTE(""),
    DTI9REC(""),
    DTLEAVEEND(""),
    DTORGEMPLOY(""),
    DTPEREMPFRM(""),
    DTPEREMPTO(""),
    DTTRANMEMREC(""),
    DTW4SIGNED("W4 Signed"),
    MOADDCITYTAX(""),
    MOADDFEDTAX(""),
    MOADDSTATTAX(""),
    MOADDYONTAX(""),
    MOAMTEXCEED(""),
    MOCREDUN(""),
    MOHRHRSPD(""),
    MOLOSTTIME(""),
    MOPRIORYRTE(""),
    MOSALBIWKLY("Bi-weekly Salary"),
    MOTOTHRSPD("Total Hours Spent"),
    MOVACHRSPD("Total Vacation Hours Spent"),
    NAFIRST("First Name"),
    NAFIRSTSUP("Supervisor's First Name"),
    NALAST("Last Name"),
    NALASTSUP("Supervisor's Last Name"),
    NAMIDINIT("Middle Initial"),
    NATITLE("Title"),
    NUCITYTAXEX(""),
    NUDAYSLOST(""),
    NUFEDTAXEX(""),
    NUHRHRSPD(""),
    NULINE("Line Number"),
    NUMINTOTEND("Minimum Hours Remaining"),
    NUMINTOTHRS("Minimum Hours Total"),
    NUPOSITION("Position Number"),
    NUSTATTAXEX(""),
    NUVACHRSLF(""),
    NUVACHRSPD(""),
    NUXREFSV("Supervisor Id"),
    NUYONTAXEX("");
    
    private String desc;

    TransactionColumn(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isValidColumn(String column) {
        try {
            TransactionColumn.valueOf(column);
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
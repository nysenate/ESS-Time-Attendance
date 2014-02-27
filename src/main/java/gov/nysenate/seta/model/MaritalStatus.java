package gov.nysenate.seta.model;

public enum MaritalStatus
{
    SINGLE('S', "Single"),
    DIVORCED('D', "Divorced"),
    MARRIED('M', "Married"),
    WIDOWED('W', "Widowed");

    char code;
    String desc;

    MaritalStatus(char code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MaritalStatus valueOfCode(char code) {
        for (MaritalStatus status : MaritalStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public char getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

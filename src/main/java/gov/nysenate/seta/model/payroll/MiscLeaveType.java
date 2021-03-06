package gov.nysenate.seta.model.payroll;

import gov.nysenate.common.OutputUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Miscellaneous leave time must be accompanied by a code representing the type of leave.
 * This is an enumeration of all those various codes with condensed and full descriptions.
 */
public enum MiscLeaveType
{
    ADMINISTRATIVE_LEAVE("A", "Administrative Leave", "Administrative Leave", new BigInteger("307455737086830401923262932905351643162")),
    BEREAVEMENT_LEAVE("B","Bereavement Leave", "Bereavement Leave", new BigInteger("191601132010624858613098950383796367496")),
    BRST_PROST_CANCER_SCREENING("C", "Brst, Prost Cancr Scrning", "Breast and Prostate Cancer Screening Leave", new BigInteger("191601132010620022909820491867097542792")),
    BLOOD_DONATION("D", "Blood Donation", "Blood Donation Leave", new BigInteger("191601132010628485390557794271320486024")),
    EXTRAORDINARY_LEAVE("E", "Extraordinary Leave", "Leave for Extraordinary Circumstances", new BigInteger("191601132010623649687279335754621661320")),
    JURY_LEAVE("J", "Jury Leave", "Jury Leave", new BigInteger("191601132010621231835640106496272248968")),
    MILITARY_LEAVE("M", "Military Leave", "Military Leave", new BigInteger("191601132010626067538918565012971073672")),
    OTHER_LVS_MANDATED("O", "Other Leaves", "Other Leaves Mandated By Law", new BigInteger("191601132010627276464738179642145779848")),
    PARENTAL_LEAVE("P", "Parental Leave", "Parental Leave", new BigInteger("191601132010616396132361647979573424264")),
    SICK_LEAVE_HALF_PAY("S", "Sick Leave With Half Pay", "Sick Leave With Half Pay", new BigInteger("191601132010622440761459721125446955144")),
    WITNESS_LEAVE("W", "Witness Leave", "Witness Leave", new BigInteger("191601132010618813984000877237922836616")),
    VOL_FIRE_EMERG_MED_ACTIVITY("V", "Vol Fire, Emerg, Med Activ", "Volunteer Fire Fighting and Emergency Medical Leave", new BigInteger("191601132010629694316377408900495192200")),
    EXTENDED_SICK_LEAVE("X", "Extended Sick Leave", "Extended Sick Leave", new BigInteger("191601132010617605058181262608748130440"));

    String code;
    String shortName;
    String name;
    BigInteger miscLeaveId;

    MiscLeaveType(String code, String shortName, String name, BigInteger miscLeaveId) {
        this.code = code;
        this.shortName = shortName;
        this.name = name;
        this.miscLeaveId = miscLeaveId;
    }

    public static MiscLeaveType valueOfCode(String code) {
        for (MiscLeaveType leaveType : MiscLeaveType.values()) {
            if (leaveType.code.equals(code)) {
                return leaveType;
            }
        }
        return null;
    }

    public static MiscLeaveType valueOfId(BigInteger miscLeaveId){
        for (MiscLeaveType leaveType : MiscLeaveType.values()) {
            if (leaveType.miscLeaveId.equals(miscLeaveId)) {
                return leaveType;
            }
        }
        return null;
    }

    public static String getJsonLabels() {
        Map<String, String> labelMap = new HashMap<>();
        for (MiscLeaveType leaveType : MiscLeaveType.values()) {
            labelMap.put(leaveType.name(), leaveType.shortName);
        }
        return OutputUtils.toJson(labelMap);
    }

    public String getCode() {
        return code;
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    public BigInteger getMiscLeaveId() {
        return miscLeaveId;
    }
}

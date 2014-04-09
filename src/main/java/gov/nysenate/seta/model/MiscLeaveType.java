package gov.nysenate.seta.model;

import gov.nysenate.seta.dao.MigrationDao;

/**
 * Miscellaneous leave time must be accompanied by a code representing the type of leave.
 * This is an enumeration of all those various codes with condensed and full descriptions.
 */
public enum MiscLeaveType
{
    BEREAVEMENT_LEAVE("B","Bereavement Leave", "Bereavement Leave"),
    BRST_PROST_CANCER_SCREENING("C", "Brst, Prost Cancr Scrning", "Breast and Prostate Cancer Screening Leave"),
    BLOOD_DONATION("D", "Blood Donation", "Blood Donation Leave"),
    EXTRAORDINARY_LEAVE("E", "Extraordinary Leave", "Leave for Extraordinary Circumstances"),
    JURY_LEAVE("J", "Jury Leave", "Jury Leave"),
    MILITARY_LEAVE("M", "Military Leave", "Military Leave"),
    OTHER_LVS_MANDATED("O", "Other Leaves", "Other Leaves Mandated By Law"),
    PARENTAL_LEAVE("P", "Parental Leave", "Parental Leave"),
    SICK_LEAVE_HALF_PAY("S", "Sick Leave With Half Pay", "Sick Leave With Half Pay"),
    WITNESS_LEAVE("W", "Witness Leave", "Witness Leave"),
    VOL_FIRE_EMERG_MED_ACTIVITY("V", "Vol Fire, Emerg, Med Activ", "Volunteer Fire Fighting and Emergency Medical Leave"),
    EXTENDED_SICK_LEAVE("X", "Extended Sick Leave", "Extended Sick Leave");

    String code;
    String shortName;
    String name;

    MiscLeaveType(String code, String shortName, String name) {
        this.code = code;
        this.shortName = shortName;
        this.name = name;
    }

    public static MiscLeaveType valueOfCode(String code) {
        for (MiscLeaveType leaveType : MiscLeaveType.values()) {
            if (leaveType.code==code) {
                return leaveType;
            }
        }
        return null;
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
}

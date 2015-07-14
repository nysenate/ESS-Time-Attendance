package gov.nysenate.seta.client.view;

import gov.nysenate.seta.client.view.base.ViewObject;
import gov.nysenate.seta.model.transaction.TransactionRecord;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
public class EmpTransRecordView implements ViewObject
{
    protected int employeeId;
    protected boolean active;
    protected String transCode;
    protected String transDesc;
    protected Map<String, String> valueMap;
    protected LocalDate effectDate;
    protected LocalDateTime originalDate;
    protected LocalDateTime updateDate;
    protected String note;

    public EmpTransRecordView(TransactionRecord record) {
        if (record != null) {
            this.employeeId = record.getEmployeeId();
            this.active = record.isActive();
            this.transCode = record.getTransCode().name();
            this.transDesc = record.getTransCode().getDesc();
            this.valueMap = new HashMap<>(record.getValueMap());
            this.effectDate = record.getEffectDate();
            this.originalDate = record.getOriginalDate();
            this.updateDate = record.getUpdateDate();
            this.note = record.getNote();
        }
    }

    @Override
    public String getViewType() {
        return "emp-transaction-record";
    }

    @XmlElement
    public int getEmployeeId() {
        return employeeId;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public String getTransCode() {
        return transCode;
    }

    @XmlElement
    public String getTransDesc() {
        return transDesc;
    }

    @XmlElement
    public Map<String, String> getValueMap() {
        return valueMap;
    }

    @XmlElement
    public LocalDate getEffectDate() {
        return effectDate;
    }

    @XmlElement
    public LocalDateTime getOriginalDate() {
        return originalDate;
    }

    @XmlElement
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    @XmlElement
    public String getNote() {
        return note;
    }
}

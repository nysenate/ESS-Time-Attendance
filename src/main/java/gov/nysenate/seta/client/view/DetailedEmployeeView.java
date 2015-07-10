package gov.nysenate.seta.client.view;

import gov.nysenate.seta.model.personnel.Employee;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DetailedEmployeeView extends EmployeeView
{
    protected int supervisorId;
    protected String jobTitle;
    protected String payType;
    protected RespCenterView respCtr;
    protected AddressView workAddress;

    public DetailedEmployeeView(Employee employee) {
        super(employee);
        this.supervisorId = employee.getSupervisorId();
        this.jobTitle = employee.getJobTitle();
        this.payType = (employee.getPayType() != null) ? employee.getPayType().name() : "";
        this.respCtr = new RespCenterView(employee.getRespCenter());
        if (employee.getWorkLocation() != null) {
            this.workAddress = new AddressView(employee.getWorkLocation().getAddress());
        }
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "employee-detail";
    }

    @XmlElement
    public int getSupervisorId() {
        return supervisorId;
    }

    @XmlElement
    public String getJobTitle() {
        return jobTitle;
    }

    @XmlElement
    public String getPayType() {
        return payType;
    }

    @XmlElement
    public RespCenterView getRespCtr() {
        return respCtr;
    }

    @XmlElement
    public AddressView getWorkAddress() {
        return workAddress;
    }
}

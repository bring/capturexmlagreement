package no.bring.priceengine.dao;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "surchargedump",schema="core")
public class Customerbranch implements Serializable {


    @Id
    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "branch")
    private String branch;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}

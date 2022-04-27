package no.bring.priceengine.dao;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "customervolfactordump",schema="core")
public class Customervolfactordump implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "core.customervolfactordump_serialid", name = "serialidSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serialidSeq")
    @Column(name = "id")
    private Integer id;

    @Column(name="customer_name")
    private String customerName;

    @Column(name="branch")
    private Integer branch;

    @Column(name="customer_number")
    private String customerNumber;

    @Column(name="service")
    private Integer service;

    @Column(name="destination")
    private String destinaiton;

    @Column(name="volfactor")
    private Double volumetricFactor;

    @Column(name="updated")
    private Boolean updated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Integer getService() {
        return service;
    }

    public void setService(Integer service) {
        this.service = service;
    }

    public String getDestinaiton() {
        return destinaiton;
    }

    public void setDestinaiton(String destinaiton) {
        this.destinaiton = destinaiton;
    }

    public Double getVolumetricFactor() {
        return volumetricFactor;
    }

    public void setVolumetricFactor(Double volumetricFactor) {
        this.volumetricFactor = volumetricFactor;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }
}

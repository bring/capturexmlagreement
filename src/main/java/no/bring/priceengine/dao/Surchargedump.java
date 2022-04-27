package no.bring.priceengine.dao;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "surchargedump",schema="core")
public class Surchargedump implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "core.surchargedump_serialid", name = "serialidSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serialidSeq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "customer_name")
    private String customerName;

    //
    @Column(name = "sulphur_parcel")
    private String sulphurParcel;

    @Column(name = "sulphur_parcel_pallet")
    private String sulphurParcelPallet;

    @Column(name = "fuel_parcel")
    private String fuelParcel;

    @Column(name = "fuel_pallet")
    private String fuelPallet;

    @Column(name = "percentage_fuel_parcel")
    private String percentageFuelParcel;

    @Column(name = "percentage_fuel_pallet")
    private String percentageFuelPallet;

    @Column(name = "Updated")
    private boolean updated;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "branch")
    private Integer branch;

    @Column(name = "remark")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSulphurParcel() {
        return sulphurParcel;
    }

    public void setSulphurParcel(String sulphurParcel) {
        this.sulphurParcel = sulphurParcel;
    }

    public String getSulphurParcelPallet() {
        return sulphurParcelPallet;
    }

    public void setSulphurParcelPallet(String sulphurParcelPallet) {
        this.sulphurParcelPallet = sulphurParcelPallet;
    }

    public String getFuelParcel() {
        return fuelParcel;
    }

    public void setFuelParcel(String fuelParcel) {
        this.fuelParcel = fuelParcel;
    }

    public String getFuelPallet() {
        return fuelPallet;
    }

    public void setFuelPallet(String fuelPallet) {
        this.fuelPallet = fuelPallet;
    }

    public String getPercentageFuelParcel() {
        return percentageFuelParcel;
    }

    public void setPercentageFuelParcel(String percentageFuelParcel) {
        this.percentageFuelParcel = percentageFuelParcel;
    }

    public String getPercentageFuelPallet() {
        return percentageFuelPallet;
    }

    public void setPercentageFuelPallet(String percentageFuelPallet) {
        this.percentageFuelPallet = percentageFuelPallet;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

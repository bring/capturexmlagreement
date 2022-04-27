package no.bring.priceengine.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "percentagebaseddeltadump", schema = "core")
public class Percentagebaseddeltadump implements Serializable {


    @Column(name = "branch")
    private Integer branch;
    @Id
    @Column(name = "parent_customer_number")
    private String parentCustomerNumber;

    @Column(name = "parent_customer_name")
    private String parentCustomerName;
    @Id
    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "customer_name")
    private String customerName;
    @Id
    @Column(name = "prodno")
    private Integer prodno;
    @Id
    @Column(name = "startdate")
    private Date startdate;
    @Id
    @Column(name = "enddate")
    private Date enddate;
    @Id
    @Column(name = "routetype")
    private String routeType;
    @Id
    @Column(name = "from_location")
    private String fromLocation;
    @Id
    @Column(name = "to_location")
    private String toLocation;
    @Id
    @Column(name = "precentage_discount")
    private String precentageDiscount;

    @Column(name = "updated")
    private Boolean updated;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "filecountry")
    private String fileCountry;

    @Column(name = "zone_type")
    private String zoneType;

    @Column(name = "remark")
    private String remark;

    @Column(name = "price_id")
    private Long priceId;

    @Column(name = "created_dt")
    private Date createdDt;


    public Date getCreatedDt() {
        return createdDt;
    }



    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public String getParentCustomerNumber() {
        return parentCustomerNumber;
    }

    public void setParentCustomerNumber(String parentCustomerNumber) {
        this.parentCustomerNumber = parentCustomerNumber;
    }

    public String getParentCustomerName() {
        return parentCustomerName;
    }

    public void setParentCustomerName(String parentCustomerName) {
        this.parentCustomerName = parentCustomerName;
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

    public Integer getProdno() {
        return prodno;
    }

    public void setProdno(Integer prodno) {
        this.prodno = prodno;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getPrecentageDiscount() {
        return precentageDiscount;
    }

    public void setPrecentageDiscount(String precentageDiscount) {
        this.precentageDiscount = precentageDiscount;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getFileCountry() {
        return fileCountry;
    }

    public void setFileCountry(String fileCountry) {
        this.fileCountry = fileCountry;
    }

    public String getZoneType() {
        return zoneType;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }
}

package no.bring.priceengine.dao;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "percentagebaseddump",schema="core")
public class Percentagebaseddump  implements Serializable {



    @Id
    @SequenceGenerator(sequenceName = "core.percentagebaseddump_serialid", name = "serialidSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serialidSeq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "branch")
    private Integer branch;

    @Column(name = "parent_customer_number")
    private String parentCustomerNumber;

    @Column(name = "parent_customer_name")
    private String parentCustomerName;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "prodno")
    private Integer prodno;

    @Column(name = "startdate")
    private Date startdate;

    @Column(name = "enddate")
    private Date enddate;

    @Column(name = "routetype")
    private String routeType;

    @Column(name = "from_location")
    private String fromLocation;

    @Column(name = "to_location")
    private String toLocation;

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
    private Integer remark;

    @Column(name = "price_id")
    private Integer priceId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getRemark() {
        return remark;
    }

    public void setRemark(Integer remark) {
        this.remark = remark;
    }

    //    @Override
//    public String toString() {
//        return "product [id=" + product + "]";
//    }
//
//    @Override
//    public int compareTo(Percentagebaseddump percentagebaseddump) {
//        if(percentagebaseddump.getProduct()!=null)
//            return this.getProduct().compareTo(percentagebaseddump.getProduct());
//        else
//            return 0;
//    }


    public Integer getPriceId() {
        return priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }
}

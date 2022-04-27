package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "serviceaddon", schema = "core")
@PrimaryKeyJoinColumn(name = "item_id")
@DiscriminatorValue(value = "2")
@Cacheable(true)
public class ServiceAddOn  extends Item {

    @ManyToOne
    @JoinColumn(name = "addon_item_id")
    @JsonBackReference
    private AddOn addOn;

    @Column(name = "a_source_system_record_pk")
    private Long aSourceSystemRecordPk;

    @Column(name = "i_source_system_record_pk")
    private Long iSourceSystemRecordPk;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "service_item_id")
    @JsonBackReference
    private Service service;

    @Column(name = "serviceaddon_name")
    private String serviceAddOnName;

    @Column(name = "s_source_system_record_pk")
    private Long sSourceSystemRecordPk;

//    @Column(name = "oebs_item_cat_cdItemId")
//    @XmlTransient
//    private Long oebsItemCatCdItemId;



    public ServiceAddOn() {
    }

    public AddOn getAddOn() {
        return addOn;
    }

    public Long getaSourceSystemRecordPk() {
        return aSourceSystemRecordPk;
    }

    public Service getService() {
        return service;
    }

    public String getServiceAddOnName() {
        return serviceAddOnName;
    }

    public Long getsSourceSystemRecordPk() {
        return sSourceSystemRecordPk;
    }

    public void setAddOn(AddOn addOn) {
        this.addOn = addOn;
    }

    public void setaSourceSystemRecordPk(Long aSourceSystemRecordPk) {
        this.aSourceSystemRecordPk = aSourceSystemRecordPk;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setServiceAddOnName(String serviceAddOnName) {
        this.serviceAddOnName = serviceAddOnName;
    }

    public void setsSourceSystemRecordPk(Long sSourceSystemRecordPk) {
        this.sSourceSystemRecordPk = sSourceSystemRecordPk;
    }

//    public Long getOebsItemCatCdItemId() {
//        return oebsItemCatCdItemId;
//    }
//
//    public void setOebsItemCatCdItemId(Long oebsItemCatCdItemId) {
//        this.oebsItemCatCdItemId = oebsItemCatCdItemId;
//    }

    public Long getiSourceSystemRecordPk() {
        return iSourceSystemRecordPk;
    }

    public void setiSourceSystemRecordPk(Long iSourceSystemRecordPk) {
        this.iSourceSystemRecordPk = iSourceSystemRecordPk;
    }
}

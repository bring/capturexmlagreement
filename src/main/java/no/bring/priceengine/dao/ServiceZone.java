package no.bring.priceengine.dao;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "servicezone", schema = "core")
@PrimaryKeyJoinColumn(name = "item_id")
@DiscriminatorValue(value = "4")
@Cacheable(true)
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ServiceZone extends Item {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_item_id")
    @JsonManagedReference
    @XmlTransient
    private Service service;

    @Column(name = "servicezone_name")
    @XmlTransient
    private String serviceZoneName;

    @Column(name = "zone_number")
    @XmlTransient
    private Long zoneNumber;


    @Column(name = "oebs_item_cat_cd_item_id")
    @XmlTransient
    private Long oebsItemCatCdItemId;


    protected ServiceZone() {
    }

    public Service getService() {
        return service;
    }

    public String getServiceZoneName() {
        return serviceZoneName;
    }

    public Long getZoneNumber() {
        return zoneNumber;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setServiceZoneName(String serviceZoneName) {
        this.serviceZoneName = serviceZoneName;
    }

    public void setServiz(Service service) {
        this.service = service;
    }

    public void setZoneNumber(Long zoneNumber) {
        this.zoneNumber = zoneNumber;
    }

    public Long getOebsItemCatCdItemId() {
        return oebsItemCatCdItemId;
    }

    public void setOebsItemCatCdItemId(Long oebsItemCatCdItemId) {
        this.oebsItemCatCdItemId = oebsItemCatCdItemId;
    }

}

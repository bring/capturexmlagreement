package no.bring.priceengine.dao;


import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service", schema = "core")
@PrimaryKeyJoinColumn(name = "item_id")
@DiscriminatorValue(value = "3")
@Cacheable(true)
public class Service extends Item implements Serializable {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "max_chargeable_price")
    private Long maximumChargeablePrice;

    @Column(name = "min_chargeable_price")
    private Long minimumChargeablePrice;

    @Column(name = "min_chargeable_weight")
    private Long minimumChargeableWeight;

    @Column(name = "price_tag_level_tp_cd")
    private Long priceTagLevelTpCd;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "source_system_record_pk")
    private Long sourceSystemRecordPk;

/*	@Column(name = "is_npb_new")
	private boolean isNpbNew;


	@Column(name = "is_b2b")
	private boolean isB2b;*/

    @OneToMany(mappedBy = "service")
    @JsonBackReference
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<ServiceZone> serviceZones = new HashSet<>();

    @Column(name = "volumetric_factor")
    private BigDecimal volumetricFactor;

    @Column(name = "volumetricfactor_tp_cd")
    private BigDecimal volumetricFactorTpCd;

    @Column(name = "is_postalcode_reqd")
    private Boolean isPostalcodeReqd;

    @Column(name = "is_crossborder_service")
    private Boolean isCrossBorderService;



    public Service() {
    }

    public Long getMaximumChargeablePrice() {
        return maximumChargeablePrice;
    }

    public Long getMinimumChargeablePrice() {
        return minimumChargeablePrice;
    }

    public Long getMinimumChargeableWeight() {
        return minimumChargeableWeight;
    }


    public String getServiceName() {
        return serviceName;
    }

    public Set<ServiceZone> getServiceZones() {
        return serviceZones;
    }

    public BigDecimal getVolumetricFactor() {
        return volumetricFactor;
    }

    public void setMaximumChargeablePrice(Long maximumChargeablePrice) {
        this.maximumChargeablePrice = maximumChargeablePrice;
    }



    public Boolean getIsPostalcodeReqd() {
        return isPostalcodeReqd;
    }

    public void setIsPostalcodeReqd(Boolean isPostalcodeReqd) {
        this.isPostalcodeReqd = isPostalcodeReqd;
    }

    public Boolean getIsCrossBorderService() {
        return isCrossBorderService;
    }

    public void setIsCrossBorderService(Boolean isCrossBorderService) {
        this.isCrossBorderService = isCrossBorderService;
    }

    public void setMinimumChargeablePrice(Long minimumChargeablePrice) {
        this.minimumChargeablePrice = minimumChargeablePrice;
    }

    public void setMinimumChargeableWeight(Long minimumChargeableWeight) {
        this.minimumChargeableWeight = minimumChargeableWeight;
    }

    public void setPriceTagLevelTpCd(Long priceTagLevelTpCd) {
        this.priceTagLevelTpCd = priceTagLevelTpCd;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceZones(Set<ServiceZone> serviceZones) {
        this.serviceZones = serviceZones;
    }

    public void setVolumetricFactor(BigDecimal volumetricFactor) {
        this.volumetricFactor = volumetricFactor;
    }

    public BigDecimal getVolumetricFactorTpCd() {
        return volumetricFactorTpCd;
    }

    public void setVolumetricFactorTpCd(BigDecimal volumetricFactorTpCd) {
        this.volumetricFactorTpCd = volumetricFactorTpCd;
    }


    public Long getSourceSystemRecordPk() {
        return sourceSystemRecordPk;
    }

    public void setSourceSystemRecordPk(Long sourceSystemRecordPk) {
        this.sourceSystemRecordPk = sourceSystemRecordPk;
    }
/*	public boolean isNpbNew() {
		return isNpbNew;
	}

	public void setNpbNew(boolean isNpbNew) {
		this.isNpbNew = isNpbNew;
	}

	public boolean isB2b() {
		return isB2b;
	}

	public void setB2b(boolean isB2b) {
		this.isB2b = isB2b;
	}*/


    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}

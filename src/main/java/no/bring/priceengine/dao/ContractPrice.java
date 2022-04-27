package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@Entity
@Table(name = "contractprice", schema = "core")
@PrimaryKeyJoinColumn(name = "price_id")
@DiscriminatorValue(value = "3")
@Cacheable(true)
public class ContractPrice extends Price implements Serializable {

    public static final String SPOT_PRICING = "SpotPricing";
// PRESENT IN DATABASE BUT NOT IN THIS CLASS -   source_reference
    @Id
    @Column(name = "price_id")
    private Long priceId;//

    @Column(name = "sequence")
    private Long sequence;//

    @Column(name = "customer_currency")
    private String currency;//

    @Column(name = "contractprice_application_tp_cd")
    private Long contractPriceApplicationTpCd;//


    @Column(name = "contractprice_adjustment_tp_cd")
    private Long contractPriceAdjustmentTpCd;//


    @Column(name = "priority")
    private Long priority;//


    @Column(name = "formula")
    private String formula;//


    @Column(name = "contractprice_st_tp_cd")
    private Integer contractPriceStTpCd;//


    @Column(name = "item_id_dup")
    private Long itemIdDup;

    @Column(name = "from_country_tp_cd")
    private Integer fromCountry; //

    @Column(name = "to_country_tp_cd")
    private Integer toCountry;//

    @Column(name = "contractcomponent_id")
    private Integer contractcomponentId;


    @Column(name = "from_country_postal_code")
    //commented by Swarnim private String[] fromPostalCode;
    private String fromPostalCode;  //


    @Column(name = "to_country_postal_code")
    //commented by Swarnim private String[] toPostcalCode;
    private String toPostcalCode;//

    @Column(name = "to_country_zone_count_from")
    private Integer toCountryZoneCountFrom;//

    @Column(name = "to_country_zone_count_to")
    private Integer toCountryZoneCountTo;

    @Column(name = "appl_journey_tp_cd")
    private Integer applJourneyTpCd;

    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "contractprice_processing_tp_cd")
    private Integer contractpriceProcessingTpCd;

    @Column(name = "from_routeid")
    private Integer fromRouteId;

    @Column(name = "to_routeid")
    private Integer toRouteId;

    @XmlTransient
    @Transient
    private String applicabilityCriteriaJSonString;

    @XmlTransient
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicabilitycriteria_id" )
    private ApplicabilityCriteriaEntity applicabilityCriteriaEntity;//


//    @ManyToOne(cascade = { CascadeType.ALL })
//    @JoinColumn(name = "contractcomponent_id")
//    @JsonBackReference
//    private ContractComponent contractComponent;//

//    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "contractcomponent_id")
    @JsonBackReference
    private Integer contractComponent;//


    public ContractPrice(){

    }

    public ContractPrice(Long itemId){
        this.itemIdDup = itemId;
    }

    @Transient
    public String contractNumber;

//    @Transient
//    public String getContractNumber() {
//        if (contractComponent != null) {
//            return contractComponent.getContract().getSourceSystemRecordPk().toString();
//        } else {
//            return contractNumber;
//        }
//    }

    public Long getSequence() {
        if(sequence == null)
            return Long.valueOf(1);
        else
            return sequence;
    }



    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }



    public Long getContractPriceApplicationTpCd() {
        return contractPriceApplicationTpCd;
    }



    public void setContractPriceApplicationTpCd(Long contractPriceApplicationTpCd) {
        this.contractPriceApplicationTpCd = contractPriceApplicationTpCd;
    }



    public Long getContractPriceAdjustmentTpCd() {
        return contractPriceAdjustmentTpCd;
    }



    public void setContractPriceAdjustmentTpCd(Long contractPriceAdjustmentTpCd) {
        this.contractPriceAdjustmentTpCd = contractPriceAdjustmentTpCd;
    }


    public Long getPriority() {

			/*if(priority == null)
				return Long.valueOf(-9999);
			else*/
        return priority;
    }



    public void setPriority(Long priority) {
        this.priority = priority;
    }



    public String getFormula() {
        return formula;
    }



    public void setFormula(String formula) {
        this.formula = formula;
    }


//
//    public ContractComponent getContractComponent() {
//        return contractComponent;
//    }
//
//
//
//    public void setContractComponent(ContractComponent contractComponent) {
//        this.contractComponent = contractComponent;
//    }


    public Integer getContractComponent() {
        return contractComponent;
    }

    public void setContractComponent(Integer contractComponent) {
        this.contractComponent = contractComponent;
    }

    public Integer getContractPriceStTpCd() {
        return contractPriceStTpCd;
    }



    public void setContractPriceStTpCd(Integer contractPriceStTpCd) {
        this.contractPriceStTpCd = contractPriceStTpCd;
    }


    public Long getItemIdDup() {
        return itemIdDup;
    }


    public void setItemIdDup(Long itemIdDup) {
        this.itemIdDup = itemIdDup;
    }


    public String getApplicabilityCriteriaJSonString() {
        return applicabilityCriteriaJSonString;
    }


    public void setApplicabilityCriteriaJSonString(String applicabilityCriteriaJSonString) {
        this.applicabilityCriteriaJSonString = applicabilityCriteriaJSonString;
    }

    public Integer getFromCountry() {
        return fromCountry;
    }

    public void setFromCountry(Integer fromCountry) {
        this.fromCountry = fromCountry;
    }

    public Integer getToCountry() {
        return toCountry;
    }

    public void setToCountry(Integer toCountry) {
        this.toCountry = toCountry;
    }


    public String getFromPostalCode() {
        return fromPostalCode;
    }

    public void setFromPostalCode(String fromPostalCode) {
        this.fromPostalCode = fromPostalCode;
    }


    public String getToPostcalCode() {
        return toPostcalCode;
    }

    public void setToPostcalCode(String toPostcalCode) {
        this.toPostcalCode = toPostcalCode;
    }

    public Integer getToCountryZoneCountFrom() {
        return toCountryZoneCountFrom;
    }

    public void setToCountryZoneCountFrom(Integer toCountryZoneCountFrom) {
        this.toCountryZoneCountFrom = toCountryZoneCountFrom;
    }

    public Integer getToCountryZoneCountTo() {
        return toCountryZoneCountTo;
    }

    public void setToCountryZoneCountTo(Integer toCountryZoneCountTo) {
        this.toCountryZoneCountTo = toCountryZoneCountTo;
    }

    @Override
    public Long getPriceId() {
        return priceId;
    }

    @Override
    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    @PostLoad
    private void onLoad() {
        if (this.applicabilityCriteriaEntity != null ) {
            this.applicabilityCriteriaJSonString = this.applicabilityCriteriaEntity.getApplicabilityCriteriaJSONString();
        }

    }

    @Transient
    public boolean isSpotDiscount() {
        return SPOT_PRICING.equals(contractNumber);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getApplJourneyTpCd() {
        return applJourneyTpCd;
    }

    public void setApplJourneyTpCd(Integer applJourneyTpCd) {
        this.applJourneyTpCd = applJourneyTpCd;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getContractpriceProcessingTpCd() {
        return contractpriceProcessingTpCd;
    }

    public void setContractpriceProcessingTpCd(Integer contractpriceProcessingTpCd) {
        this.contractpriceProcessingTpCd = contractpriceProcessingTpCd;
    }

    public Integer getFromRouteId() {
        return fromRouteId;
    }

    public void setFromRouteId(Integer from_routeId) {
        this.fromRouteId = from_routeId;
    }

    public Integer getToRouteId() {
        return toRouteId;
    }

    public void setToRouteId(Integer to_routeId) {
        this.toRouteId = to_routeId;
    }

    public Integer getContractcomponentId() {
        return contractcomponentId;
    }

    public void setContractcomponentId(Integer contractcomponentId) {
        this.contractcomponentId = contractcomponentId;
    }
}

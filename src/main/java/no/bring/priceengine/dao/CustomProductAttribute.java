package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "customproductattribute", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class CustomProductAttribute implements Serializable {
    @Id
    @SequenceGenerator(sequenceName = "core.customproductattribute_customproductattribute_id_seq", name = "CustomProductAttributeIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CustomProductAttributeIdSequence")
    @Column(name = "customproductattribute_id")
    private Long customProductAttributeId;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "contractcomponent_id")
    @JsonBackReference
    private ContractComponent contractComponent;

    //@Column(name = "custom_attributes")

//    @Type(type = "jsonb")
//    @Column(columnDefinition = "custom_attributes")
//    private CustomAttributes customAttributes;

    @JsonIgnore
    @Transient
    CustomAttributesContainer customAttributesContainer;

    @JsonIgnore
    @Transient
    boolean isCustomAttributesContainerMapped;

    @Column(name = "start_dt")
    @NotNull
    LocalDate startDt;

    @Column(name = "end_dt")
    @NotNull
    LocalDate endDt;

    @Column(name = "customproductattribute_st_tp_cd")
    Integer customProductattributeStTpCd;

    @Column(name = "item_id")
    private Long itemId;

/*	@Column(name = "appl_terminal_set")
	private String applTerminalSet;*/


  //  @Column(name = "appl_t1_set")
  //  private Short[] applT1Set;

 //   @Column(name = "appl_t2_set")
  //  private String applT2Set;

    @XmlTransient
    @Transient
    private String applicabilityCriteriaJSONString;

    @XmlTransient
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicabilitycriteria_id" )
    public ApplicabilityCriteriaEntity applicabilityCriteriaEntity;


//    @Column(name = "appl_direction_tp_cd")
//    private Long applDirectionTpCd;

//    @Column(name = "appl_region_tp_cd")
//    private Long applRegionTpCd;

    @Column(name = "appl_journey_tp_cd")
    private Integer applJourneyTpCd;


    public Long getCustomProductAttributeId() {
        return customProductAttributeId;
    }

    public void setCustomProductAttributeId(Long customProductAttributeId) {
        this.customProductAttributeId = customProductAttributeId;
    }

    public ContractComponent getContractComponent() {
        return contractComponent;
    }

    public void setContractComponent(ContractComponent contractComponent) {
        this.contractComponent = contractComponent;
    }

//    public String getCustomAttributesJSONString() {
//        return customAttributesJSONString;
//    }
//
//    public void setCustomAttributesJSONString(String customAttributesJSONString) {
//        this.customAttributesJSONString = customAttributesJSONString;
//    }

    public LocalDate getStartDt() {
        return startDt;
    }

    public void setStartDt(LocalDate startDt) {
        this.startDt = startDt;
    }

    public LocalDate getEndDt() {
        return endDt;
    }

    public void setEndDt(LocalDate endDt) {
        this.endDt = endDt;
    }

    public Integer getCustomProductattributeStTpCd() {
        return customProductattributeStTpCd;
    }

    public void setCustomProductattributeStTpCd(Integer customProductattributeStTpCd) {
        this.customProductattributeStTpCd = customProductattributeStTpCd;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

//    public Short[] getApplT1Set() {
//        return applT1Set;
//    }
//
//    public void setApplT1Set(Short[] applT1Set) {
//        this.applT1Set = applT1Set;
//    }

//    public String getApplT2Set() {
//        return applT2Set;
//    }
//
//    public void setApplT2Set(String applT2Set) {
//        this.applT2Set = applT2Set;
//    }

//    public Long getApplDirectionTpCd() {
//        return applDirectionTpCd;
//    }
//
//    public void setApplDirectionTpCd(Long applDirectionTpCd) {
//        this.applDirectionTpCd = applDirectionTpCd;
//    }

//    public Long getApplRegionTpCd() {
//        return applRegionTpCd;
//    }
//
//    public void setApplRegionTpCd(Long applRegionTpCd) {
//        this.applRegionTpCd = applRegionTpCd;
//    }

    public CustomAttributesContainer getCustomAttributesContainer() {

        ObjectMapper mapper = new ObjectMapper();

        try {

//            if( !isCustomAttributesContainerMapped &&  customAttributesContainer == null &&  !StringUtils.isEmpty(customAttributesJSONString.trim()) )
//            {
//                CustomAttributesContainer cacRetrieved = mapper.readValue(this.customAttributesJSONString,
//                        CustomAttributesContainer.class);
//                customAttributesContainer= cacRetrieved;
//            }


        } catch (Exception e) {
            System.out.println("Error mapping the Custom Product container ");
        }

        return customAttributesContainer;
    }

    public void setCustomAttributesContainer(CustomAttributesContainer customAttributesContainer) {
        this.customAttributesContainer = customAttributesContainer;
    }

    public boolean isCustomAttributesContainerMapped() {
        return isCustomAttributesContainerMapped;
    }

    public void setCustomAttributesContainerMapped(boolean isCustomAttributesContainerMapped) {
        this.isCustomAttributesContainerMapped = isCustomAttributesContainerMapped;
    }

    public Integer getApplJourneyTpCd() {
        return applJourneyTpCd;
    }

    public void setApplJourneyTpCd(Integer applJourneyTpCd) {
        this.applJourneyTpCd = applJourneyTpCd;
    }

//    @PostLoad
//    private void onLoad() {
//        if (this.applicabilityCriteriaEntity != null ) {
//            this.applicabilityCriteriaJSONString = this.applicabilityCriteriaEntity.getApplicabilityCriteriaJSONString();
//        }
//
//        //this.applT1Set =  this.applT1Set==null?this.applT1Set:this.applT1Set.replaceAll("\"" , "");
//      //  this.applT2Set =  this.applT2Set==null?this.applT2Set:this.applT2Set.replaceAll("\"" , "");
//
//    }

//    public CustomAttributes getCustomAttributes() {
//        return customAttributes;
//    }
//
//    public void setCustomAttributes(CustomAttributes customAttributes) {
//        this.customAttributes = customAttributes;
//    }

    public String getApplicabilityCriteriaJSONString() {
        return applicabilityCriteriaJSONString;
    }

    public void setApplicabilityCriteriaJSONString(String applicabilityCriteriaJSONString) {
        this.applicabilityCriteriaJSONString = applicabilityCriteriaJSONString;
    }

    public ApplicabilityCriteriaEntity getApplicabilityCriteriaEntity() {
        return applicabilityCriteriaEntity;
    }

    public void setApplicabilityCriteriaEntity(ApplicabilityCriteriaEntity applicabilityCriteriaEntity) {
        this.applicabilityCriteriaEntity = applicabilityCriteriaEntity;
    }
}

package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.*;


@Entity
@Table(name = "pricematrix", schema = "core")
@NamedQuery(name = "PriceMatrix.findByName", query = "select priceMatrixId,priceMatrixName from PriceMatrix pm where pm.priceMatrixName=?1")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class PriceMatrix {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceMatrix.class);

    @Transient
    @XmlTransient
    @JsonIgnore
    private PriceMatrixMapsManager priceMatrixMapsManager;

    @Id
    @SequenceGenerator(sequenceName = "core.pricematrix_pricematrix_id_seq", name = "PriceMatrixIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PriceMatrixIdSequence")
    @Column(name = "pricematrix_id")
    private Long priceMatrixId;


    @Column(name = "is_flat")
    private Boolean isFlat;

    @Column(name = "is_zone_based")
    private Boolean isZoneBased;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @Column(name = "max_zonenumber")
    private Long maxZonenumber;

    @Column(name = "min_zonenumber")
    private Long minZonenumber;

    @Column(name = "price_calc_tp_cd")
    private Long priceCalcTpCd;

    @Column(name = "pricematrix_basis_tp_cd")
    private Long priceMatrixBasisTpCd;

    @Column(name = "pricematrix_tp_cd")
    private Integer priceMatrixTpCd;

    @Column(name = "pricematrix_max_price_basis_value")
    private Long priceMatrixMaxPriceBasisValue;

    @Column(name = "pricematrix_name")
    private String priceMatrixName;

    @Column(name = "pricematrix_usage_tp_cd")
    private Integer priceMatrixUsageTpCd;


    // Initially the Data is loaded in these simple Sets and maps  by hibernate/JPA. Then they are mapped in hashMaps and TreeMaps in the onload method
    @OneToMany(mappedBy = "priceMatrix", fetch = FetchType.LAZY)
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @XmlTransient
    @JsonIgnore
    private Set<PriceMatrixEntry> priceMatrixEntries = new HashSet<>();

    @OneToMany(mappedBy = "priceMatrix", fetch = FetchType.LAZY)
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @XmlTransient
    @JsonIgnore
    private Set<PriceMatrixEntryExt> priceMatrixExtensionSet1 = new HashSet<>();

    @OneToMany(mappedBy = "priceMatrix", fetch = FetchType.LAZY)
    @MapKey(name = "priceBasisLowerBound")
    @OrderBy("price_basis_lower_bound ASC")
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @XmlTransient
    @JsonIgnore
    private Map<Long, PriceMatrixEntry> weightServicePriceMatrixEntryMap1 = new HashMap<Long, PriceMatrixEntry>();


    @OneToMany
    @JoinColumn(name = "pricematrix_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<PriceMatrixZoneTable> priceMatrixZoneTableSet = new HashSet<PriceMatrixZoneTable>();

    @Transient
    @XmlTransient
    @JsonIgnore
    private TreeMap<LocalDate, PriceMatrixZoneTable> priceMatrixCountryCodeZoneTableMap;


    // Some CPM's for Amphora have the price-Limit Per zone these are loaded here
    @OneToMany(mappedBy = "priceMatrix", fetch = FetchType.LAZY)
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @XmlTransient
    @JsonIgnore
    private Set<PriceMatrixZonePriceLimit> priceMatrixZonePriceLimitSet1 = new HashSet<>();

    // Initially the Data is loaded in Sets above  by hibernate/JPA. Then they are mapped in maps in the onload method


    // Flat Price MAtrix, have only one flat weight range,  a-la PUD , need to have only ZoneNumber to PriceMarixEntry Mapping : BEGIN
    @Transient
    @XmlTransient
    @JsonIgnore
    private Map<Long, PriceMatrixEntry> flatPriceMatrixEntriesMap = null;


    @Transient
    @XmlTransient
    @JsonIgnore
    private TreeMap<Long, PriceMatrixEntry> weightServicePriceMatrixEntryMap = null;


    @Transient
    @XmlTransient
    @JsonIgnore
    private Map<Long, TreeMap<Long, PriceMatrixEntry>> zoneNumberPriceMatrixEntriesMap = null;




    // ****************** Price MAtrix Extension: Zone Number to Additional Slab Mapping: BEGIN
    @Transient
    @XmlTransient
    @JsonIgnore
    private Map<Long, PriceMatrixEntryExt> zoneNumberPriceMatrixExtensionMap;
    // ****************** Price MAtrix Extension: Zone Number to Additional Slab Mapping: END



    // ****************** Price MAtrix Zone Limit : Zone Table to  Price Limit  Mapping : BEGIN
    @Transient
    @XmlTransient
    @JsonIgnore
    private Map<Long, PriceMatrixZonePriceLimit> zoneNumberPriceMatrixZonePriceLimitMap ;
    // ****************** Price MAtrix Zone Limit : Zone Table to  Price Limit  Mapping : END



    // ****************** Price MAtrix To Zone Table Mapping : BEGIN


    @Transient
    @XmlTransient
    @JsonIgnore
    private TreeMap<LocalDate, PriceMatrixZoneTable> priceMatrixFmlmZoneTableMap;

    @Transient
    @XmlTransient
    @JsonIgnore
    private TreeMap<LocalDate, PriceMatrixZoneTable> priceMatrixLinehaulZoneTableMap;


    @Transient
    @XmlTransient
    @JsonIgnore
    private TreeMap<LocalDate, PriceMatrixZoneTable> priceMatrixFullZoneTableMap;
    // ****************** Price MAtrix To Zone Table Mapping : END


    protected PriceMatrix() {
    }

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (!(o instanceof PriceMatrix)) {
            return false;
        }
        PriceMatrix priceMatrix = (PriceMatrix) o;
        return Objects.equals(priceMatrixId, priceMatrix.priceMatrixId)
                && Objects.equals(priceMatrixName, priceMatrix.priceMatrixName);

    }

    public Map<Long, PriceMatrixEntry> getFlatPriceMatrixEntriesMap() {
        return flatPriceMatrixEntriesMap;
    }

    public Boolean getIsFlat() {
        return isFlat;
    }

    public Boolean getIsZoneBased() {
        return isZoneBased;
    }


    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }

    public Long getMaxZonenumber() {
        return maxZonenumber;
    }


    public Long getPriceCalcTpCd() {
        return priceCalcTpCd;
    }

    public Long getPriceMatrixBasisTpCd() {
        return priceMatrixBasisTpCd;
    }

    @XmlTransient
    @JsonIgnore
    public Set<PriceMatrixEntry> getPriceMatrixEntries() {
        return priceMatrixEntries;
    }

    public PriceMatrixEntry getPriceMatrixEntryForPriceBasis(Long priceBasis) {

        PriceMatrixEntry priceMatrixEntry = null;

        TreeMap<Long, PriceMatrixEntry> weightServicePriceMatrixEntryMap = this.getWeightServicePriceMatrixEntryMap();

        if (weightServicePriceMatrixEntryMap != null)
            priceMatrixEntry = (PriceMatrixEntry) weightServicePriceMatrixEntryMap.floorEntry(priceBasis).getValue();

        return priceMatrixEntry;

    }



    @XmlTransient
    @JsonIgnore
    public Set<PriceMatrixEntryExt> getPriceMatrixExtensionSet1() {
        return priceMatrixExtensionSet1;
    }

    /*
     * public Set<ServicePriceMatrix> getServicePriceMatrix() { return
     * servicePriceMatrix; }
     *
     * public void setServicePriceMatrix(Set<ServicePriceMatrix>
     * servicePriceMatrix) { this.servicePriceMatrix = servicePriceMatrix; }
     */

    public Long getPriceMatrixId() {
        return priceMatrixId;
    }

    public Long getPriceMatrixMaxPriceBasisValue() {
        return priceMatrixMaxPriceBasisValue;
    }

    public String getPriceMatrixName() {
        return priceMatrixName;
    }

    public Integer getPriceMatrixUsageTpCd() {
        return priceMatrixUsageTpCd;
    }

    /*
     * @XmlTransient
     *
     * @JsonIgnore public Set<Price> getPriceSet() { return priceSet; }
     */

    @XmlTransient
    @JsonIgnore
    public TreeMap<Long, PriceMatrixEntry> getWeightServicePriceMatrixEntryMap() {
        return weightServicePriceMatrixEntryMap;
    }

    @XmlTransient
    @JsonIgnore
    public Map<Long, PriceMatrixEntry> getWeightServicePriceMatrixEntryMap1() {
        return weightServicePriceMatrixEntryMap1;
    }

    @XmlTransient
    @JsonIgnore
    public Map<Long, TreeMap<Long, PriceMatrixEntry>> getZoneNumberPriceMatrixEntriesMap() {
        return zoneNumberPriceMatrixEntriesMap;
    }

    @XmlTransient
    @JsonIgnore
    public Map<Long, PriceMatrixEntryExt> getZoneNumberPriceMatrixExtensionMap() {
        return zoneNumberPriceMatrixExtensionMap;
    }








/*	public Long getZoneTableFmlmId() {
		return zoneTableFmlmId;
	}

	public Long getZoneTableLineHaulId() {
		return zoneTableLineHaulId;
	}*/

    public TreeMap<LocalDate, PriceMatrixZoneTable> getPriceMatrixFullZoneTableMap() {
        return priceMatrixFullZoneTableMap;
    }

    public void setPriceMatrixFullZoneTableMap(TreeMap<LocalDate, PriceMatrixZoneTable> priceMatrixFullZoneTableMap) {
        this.priceMatrixFullZoneTableMap = priceMatrixFullZoneTableMap;
    }

    public Set<PriceMatrixZonePriceLimit> getPriceMatrixZonePriceLimitSet1() {
        return priceMatrixZonePriceLimitSet1;
    }

    public void setPriceMatrixZonePriceLimitSet(Set<PriceMatrixZonePriceLimit> priceMatrixZonePriceLimitSet1) {
        this.priceMatrixZonePriceLimitSet1 = priceMatrixZonePriceLimitSet1;
    }

    @XmlTransient
    @JsonIgnore
    public Map<Long, PriceMatrixZonePriceLimit> getZoneNumberPriceMatrixZonePriceLimitMap() {
        return zoneNumberPriceMatrixZonePriceLimitMap;
    }

    public void setZoneNumberPriceMatrixZonePriceLimitMap(
            Map<Long, PriceMatrixZonePriceLimit> zoneNumberPriceMatrixZonePriceLimitMap) {
        this.zoneNumberPriceMatrixZonePriceLimitMap = zoneNumberPriceMatrixZonePriceLimitMap;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priceMatrixId, priceMatrixName);
    }




    public void setFlatPriceMatrixEntriesMap(Map<Long, PriceMatrixEntry> flatPriceMatrixEntriesMap) {
        this.flatPriceMatrixEntriesMap = flatPriceMatrixEntriesMap;
    }

    public void setIsFlat(Boolean isFlat) {
        this.isFlat = isFlat;
    }

    public void setIsZoneBased(Boolean isZoneBased) {
        this.isZoneBased = isZoneBased;
    }

    public void setMaxZonenumber(Long maxZonenumber) {
        this.maxZonenumber = maxZonenumber;
    }

    public void setPriceCalcTpCd(Long priceCalcTpCd) {
        this.priceCalcTpCd = priceCalcTpCd;
    }

    public void setPriceMatrixBasisTpCd(Long priceMatrixBasisTpCd) {
        this.priceMatrixBasisTpCd = priceMatrixBasisTpCd;
    }

    public void setPriceMatrixEntries(Set<PriceMatrixEntry> priceMatrixEntries) {
        this.priceMatrixEntries = priceMatrixEntries;
    }

    public void setPriceMatrixExtensionSet1(Set<PriceMatrixEntryExt> priceMatrixExtension1) {
        this.priceMatrixExtensionSet1 = priceMatrixExtension1;
    }

    public void setPriceMatrixId(Long priceMatrixId) {
        this.priceMatrixId = priceMatrixId;
    }

    public void setPriceMatrixMaxPriceBasisValue(Long priceMatrixMaxPriceBasisValue) {
        this.priceMatrixMaxPriceBasisValue = priceMatrixMaxPriceBasisValue;
    }

    public void setPriceMatrixName(String priceMatrixName) {
        this.priceMatrixName = priceMatrixName;
    }

    public void setPriceMatrixUsageTpCd(Integer priceMatrixUsageTpCd) {
        this.priceMatrixUsageTpCd = priceMatrixUsageTpCd;
    }



    /*
     * public void setPriceSet(Set<Price> priceSet) { this.priceSet = priceSet;
     * }
     */

    public TreeMap<LocalDate, PriceMatrixZoneTable> getPriceMatrixCountryCodeZoneTableMap() {
        return priceMatrixCountryCodeZoneTableMap;
    }

    public void setPriceMatrixCountryCodeZoneTableMap(
            TreeMap<LocalDate, PriceMatrixZoneTable> priceMatrixCountryCodeZoneTableMap) {
        this.priceMatrixCountryCodeZoneTableMap = priceMatrixCountryCodeZoneTableMap;
    }

    public void setWeightServicePriceMatrixEntryMap(TreeMap<Long, PriceMatrixEntry> weightServicePriceMatrixEntryMap) {
        this.weightServicePriceMatrixEntryMap = weightServicePriceMatrixEntryMap;
    }

    public void setWeightServicePriceMatrixEntryMap1(Map<Long, PriceMatrixEntry> weightServicePriceMatrixEntryMap1) {
        this.weightServicePriceMatrixEntryMap1 = weightServicePriceMatrixEntryMap1;
    }

    public void setZoneNumberPriceMatrixEntriesMap(
            Map<Long, TreeMap<Long, PriceMatrixEntry>> zoneNumberPriceMatrixEntriesMap) {
        this.zoneNumberPriceMatrixEntriesMap = zoneNumberPriceMatrixEntriesMap;
    }

    public void setZoneNumberPriceMatrixExtensionMap(Map<Long, PriceMatrixEntryExt> zoneNumberPriceMatrixExtensionMap) {
        this.zoneNumberPriceMatrixExtensionMap = zoneNumberPriceMatrixExtensionMap;
    }

/*	public void setZoneTableFmlmId(Long zoneTableFmlmId) {
		this.zoneTableFmlmId = zoneTableFmlmId;
	}

	public void setZoneTableLineHaulId(Long zoneTableLineHaulId) {
		this.zoneTableLineHaulId = zoneTableLineHaulId;
	}*/

    public Set<PriceMatrixZoneTable> getPriceMatrixZoneTableSet() {
        return priceMatrixZoneTableSet;
    }

   public TreeMap<LocalDate, PriceMatrixZoneTable> getPriceMatrixFmlmZoneTableMap() {
        return priceMatrixFmlmZoneTableMap;
    }

    public void setPriceMatrixFmlmZoneTableMap(TreeMap<LocalDate, PriceMatrixZoneTable> priceMatrixFmlmZoneTableMap) {
        this.priceMatrixFmlmZoneTableMap = priceMatrixFmlmZoneTableMap;
    }

    public TreeMap<LocalDate, PriceMatrixZoneTable> getPriceMatrixLinehaulZoneTableMap() {
        return priceMatrixLinehaulZoneTableMap;
    }

    public void setPriceMatrixLinehaulZoneTableMap(
            TreeMap<LocalDate, PriceMatrixZoneTable> priceMatrixLinehaulZoneTableMap) {
        this.priceMatrixLinehaulZoneTableMap = priceMatrixLinehaulZoneTableMap;
    }

    public void setPriceMatrixZoneTableSet(Set<PriceMatrixZoneTable> priceMatrixZoneTableSet) {
        this.priceMatrixZoneTableSet = priceMatrixZoneTableSet;
    }


    public Integer getPriceMatrixTpCd() {
        return priceMatrixTpCd;
    }

    public void setPriceMatrixTpCd(Integer priceMatrixTpCd) {
        this.priceMatrixTpCd = priceMatrixTpCd;
    }

    public Long getMinZonenumber() {
        return minZonenumber;
    }

    public void setMinZonenumber(Long minZonenumber) {
        this.minZonenumber = minZonenumber;
    }

}

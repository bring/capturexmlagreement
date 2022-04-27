package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "price", schema = "core")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "price_tp_cd", discriminatorType = DiscriminatorType.INTEGER)
@Cacheable(true)
@NamedQueries({

        @NamedQuery(name = "Price.findForPriceByItemId", query =
                "select p from Price p where p.item.itemId = :itemId and :requestDate between p.startDt and p.endDt and p.priceTpCd = 1  ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),

        }) })
public class Price implements Serializable{

public Price(){}

    public Price(BigDecimal basePrice, Item item, Long priceDefTpCd, String createdByUser, Long priceTpCd, Integer pricePerAttributeTpCd,
                 LocalDate startDt, Integer pricePerNumber ){
        this.basePrice = basePrice;
        this.item = item;
        this.priceDefTpCd = priceDefTpCd;
        this.createdByUser = createdByUser;
        this.priceTpCd = priceTpCd;
        this.pricePerAttributeTpCd = pricePerAttributeTpCd;
        this.startDt = startDt;
        this.pricePerNumber = pricePerNumber;
    }

    /**
     *
     */
  //  private static final long serialVersionUID = -5774247203491148516L;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "end_dt")
    @NotNull
    LocalDate endDt;

    @Column(name = "created_dt")
    LocalDate createdDt;

    @XmlTransient
    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "item_id" )
    private Item item;


    /*	@Column(name = "item_id")
        Integer itemId;
    */
    @Column(name = "percentage_attribute_tp_cd")
    private Long percentageAttributeTpCd;

    @Column(name = "percent_based_price")
    private BigDecimal percentBasedPrice;

    @Column(name = "price_calc_tp_cd")
    private Long priceCalcTpCd;

    @XmlTransient
    @JsonIgnore
    @Transient
    private ComputedPriceContext priceContext;

    @Column(name = "price_def_tp_cd")
    private Long priceDefTpCd;

    @Id
    @SequenceGenerator(sequenceName = "core.price_price_id_seq", name = "PriceIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PriceIdSequence")
    @Column(name = "price_id")
    private Long priceId;

    /*	@Column(name = "price_label")
        private String priceLabel;
    */
    @Column(name = "price_lower_bound")
    private BigDecimal priceLowerBound;

    @OneToMany(mappedBy = "price")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @JsonIgnore
    private Set<PriceMatrixPrice> priceMatrixPriceSet = new HashSet<>();

    @Column(name = "price_tp_cd")
    private Long priceTpCd;

/*	@Column(name = "price_uom_tp_cd")
	private Long priceUomTpCd;*/

    @Column(name = "price_upper_bound")
    private BigDecimal priceUpperBound;

	/*@Column(name = "slab")
	private Long slab;

	@Column(name = "slab_attribute_tp_cd")
	private Long slabAttributeTpCd;*/


    //The following 2 columsn shall replace the slab and slabAttributeTpCd respectively
    @Column(name = "price_per")
    private Integer pricePerNumber;

    @Column(name = "price_per_attribute_tp_cd")
    private Integer  pricePerAttributeTpCd;


    @Column(name = "price_per_attribute_val_adj")
    private BigDecimal  pricePerAttributeValAdj;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "price")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<SlabBasedPrice> slabBasedPriceSet = new HashSet<>();


    @Column(name = "start_dt")
    @NotNull
    LocalDate startDt;

    @Column(name = "price_status_tp_cd")
    private Integer priceStatusTpCd;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "price")
    private Set<WeekdayBasedPrice> weekdayBasedPriceSet = new HashSet<>();


    @XmlTransient
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "price_alternative_item_id" )
    private Item priceSubstituteItem;

    @Column(name = "created_by_user")
    private String createdByUser;


    private Integer itemId;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public LocalDate getEndDt() {
        return endDt;
    }

    public Item getItem() {
        return item;
    }

    public Long getPercentageAttributeTpCd() {
        return percentageAttributeTpCd;
    }

    public BigDecimal getPercentBasedPrice() {
        return percentBasedPrice;
    }

    public Long getPriceCalcTpCd() {
        return priceCalcTpCd;
    }

    @XmlTransient
    @JsonIgnore
    public ComputedPriceContext getPriceContext() {
        return priceContext;
    }

    public Long getPriceDefTpCd() {
        return priceDefTpCd;
    }

    public Long getPriceId() {
        return priceId;
    }

    /*	public String getPriceLabel() {
            return priceLabel;
        }
    */
    public BigDecimal getPriceLowerBound() {
        return priceLowerBound;
    }

    public Set<PriceMatrixPrice> getPriceMatrixPriceSet() {
        return priceMatrixPriceSet;
    }

    public Long getPriceTpCd() {
        return priceTpCd;
    }

    /*public Long getPriceUomTpCd() {
        return priceUomTpCd;
    }
*/
    public BigDecimal getPriceUpperBound() {
        return priceUpperBound;
    }

    /*public Long getSlab() {
        return slab;
    }

    public Long getSlabAttributeTpCd() {
        return slabAttributeTpCd;
    }
*/
    public LocalDate getStartDt() {
        return startDt;
    }

    public Set<WeekdayBasedPrice> getWeekdayBasedPriceSet() {
        return weekdayBasedPriceSet;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public void setEndDt(LocalDate endDt) {
        this.endDt = endDt;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setPercentageAttributeTpCd(Long percentageAttributeTpCd) {
        this.percentageAttributeTpCd = percentageAttributeTpCd;
    }

    public void setPercentBasedPrice(BigDecimal percentBasedPrice) {
        this.percentBasedPrice = percentBasedPrice;
    }

    public void setPriceCalcTpCd(Long priceCalcTpCd) {
        this.priceCalcTpCd = priceCalcTpCd;
    }

    public void setPriceContext(ComputedPriceContext priceContext) {
        this.priceContext = priceContext;
    }

    public void setPriceDefTpCd(Long priceDefTpCd) {
        this.priceDefTpCd = priceDefTpCd;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    /*public void setPriceLabel(String priceLabel) {
        this.priceLabel = priceLabel;
    }
*/
    public void setPriceLowerBound(BigDecimal priceLowerBound) {
        this.priceLowerBound = priceLowerBound;
    }

    public void setPriceMatrixPriceSet(Set<PriceMatrixPrice> priceMatrixPriceSet) {
        this.priceMatrixPriceSet = priceMatrixPriceSet;
    }

    public void setPriceTpCd(Long priceTpCd) {
        this.priceTpCd = priceTpCd;

    }

    /*	public void setPriceUomTpCd(Long priceUomTpCd) {
            this.priceUomTpCd = priceUomTpCd;
        }
    */
    public void setPriceUpperBound(BigDecimal priceUpperBound) {
        this.priceUpperBound = priceUpperBound;
    }

/*	public void setSlab(Long slab) {
		this.slab = slab;
	}

	public void setSlabAttributeTpCd(Long slabAttributeTpCd) {
		this.slabAttributeTpCd = slabAttributeTpCd;
	}
*/

    public void setStartDt(LocalDate startDt) {
        this.startDt = startDt;
    }

    public void setWeekdayBasedPriceSet(Set<WeekdayBasedPrice> weekdayBasedPriceSet) {
        this.weekdayBasedPriceSet = weekdayBasedPriceSet;
    }

    public Set<SlabBasedPrice> getSlabBasedPriceSet() {
        return slabBasedPriceSet;
    }

    public void setSlabBasedPriceSet(Set<SlabBasedPrice> slabBasedPriceSet) {
        this.slabBasedPriceSet = slabBasedPriceSet;
    }

    public Item getPriceSubstituteItem() {
        return priceSubstituteItem;
    }

    public void setPriceSubstituteItem(Item priceSubstituteItem) {
        this.priceSubstituteItem = priceSubstituteItem;
    }

    public Integer getPriceStatusTpCd() {
        return priceStatusTpCd;
    }

    public void setPriceStatusTpCd(Integer priceStatusTpCd) {
        this.priceStatusTpCd = priceStatusTpCd;
    }

    public Integer getPricePerNumber() {
        return pricePerNumber;
    }

    public void setPricePerNumber(Integer pricePerNumber) {
        this.pricePerNumber = pricePerNumber;
    }

    public Integer getPricePerAttributeTpCd() {
        return pricePerAttributeTpCd;
    }

    public void setPricePerAttributeTpCd(Integer pricePerAttributeTpCd) {
        this.pricePerAttributeTpCd = pricePerAttributeTpCd;
    }

    public BigDecimal getPricePerAttributeValAdj() {
        return pricePerAttributeValAdj;
    }

    public void setPricePerAttributeValAdj(BigDecimal pricePerAttributeValAdj) {
        this.pricePerAttributeValAdj = pricePerAttributeValAdj;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public LocalDate getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(LocalDate createdDt) {
        this.createdDt = createdDt;
    }

    @Override
    public String toString() {
        return "Price [basePrice=" + basePrice + ", endDt=" + endDt + ", percentageAttributeTpCd="
                + percentageAttributeTpCd + ", percentBasedPrice=" + percentBasedPrice + ", priceCalcTpCd="
                + priceCalcTpCd + ", priceDefTpCd=" + priceDefTpCd + ", priceId=" + priceId + ", priceLowerBound="
                + priceLowerBound + ", priceTpCd=" + priceTpCd + ", priceUpperBound=" + priceUpperBound
                + ", pricePerNumber=" + pricePerNumber + ", pricePerAttributeTpCd=" + pricePerAttributeTpCd
                + ", startDt=" + startDt + "]";
    }


}
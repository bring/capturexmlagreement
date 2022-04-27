package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class PriceDAO {

public PriceDAO(){}

    public PriceDAO(BigDecimal basePrice, Item item, Long priceDefTpCd, String createdByUser, Long priceTpCd,
                    Integer pricePerAttributeTpCd,
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

    private BigDecimal basePrice;
    private LocalDate endDt;
    private Item item;

    private Long percentageAttributeTpCd;
    private BigDecimal percentBasedPrice;
    private Long priceCalcTpCd;
    private ComputedPriceContext priceContext;
    private Long priceDefTpCd;

    private Long priceId;
    private BigDecimal priceLowerBound;
    private Set<PriceMatrixPrice> priceMatrixPriceSet = new HashSet<>();

    private Long priceTpCd;

    private BigDecimal priceUpperBound;

    private Integer pricePerNumber;

    private Integer  pricePerAttributeTpCd;

    private BigDecimal  pricePerAttributeValAdj;

    private Set<SlabBasedPrice> slabBasedPriceSet = new HashSet<>();

   private  LocalDate startDt;

   private Integer priceStatusTpCd;
  private Set<WeekdayBasedPrice> weekdayBasedPriceSet = new HashSet<>();

   private Item priceSubstituteItem;

    private String createdByUser;



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

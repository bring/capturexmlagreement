package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
@Entity
@Table(name = "slabbasedpriceentry", schema = "core")
@Cacheable(true)
public class SlabBasedPriceEntry  implements Serializable, Cloneable {


    @Id
    @SequenceGenerator(sequenceName = "core.slabbasedpriceentry_slabbasedpriceentry_id_seq", name = "SlabBasedPriceEntryIdSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SlabBasedPriceEntryIdSeq")
    @Column(name = "slabbasedpriceentry_id")
    private Long slabBasedPriceEntryId;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "slabbasedprice_id")
    private SlabBasedPrice slabBasedPrice;


    @Column(name = "price_basis_lower_bound")
    private Long priceBasisLowerBound;

    @Column(name = "price_basis_upper_bound")
    private Long priceBasisUpperBound;

    @Column(name = "price")
    private BigDecimal priceValue;

    @Column(name = "default_entry")
    private boolean defaultEntry;


    public SlabBasedPriceEntry() {

    }

    public SlabBasedPriceEntry(Long priceBasisLowerBound, Long priceBasisUpperBound, BigDecimal priceValue) {
        super();
        this.priceBasisLowerBound = priceBasisLowerBound;
        this.priceBasisUpperBound = priceBasisUpperBound;
        this.priceValue = priceValue;
    }


    public Long getSlabBasedPriceEntryId() {
        return slabBasedPriceEntryId;
    }


    public void setSlabBasedPriceEntryId(Long slabBasedPriceEntryId) {
        this.slabBasedPriceEntryId = slabBasedPriceEntryId;
    }


    public SlabBasedPrice getSlabBasedPrice() {
        return slabBasedPrice;
    }


    public void setSlabBasedPrice(SlabBasedPrice slabBasedPrice) {
        this.slabBasedPrice = slabBasedPrice;
    }


    public Long getPriceBasisLowerBound() {
        return priceBasisLowerBound;
    }


    public void setPriceBasisLowerBound(Long priceBasisLowerBound) {
        this.priceBasisLowerBound = priceBasisLowerBound;
    }


    public Long getPriceBasisUpperBound() {
        return priceBasisUpperBound;
    }


    public void setPriceBasisUpperBound(Long priceBasisUpperBound) {
        this.priceBasisUpperBound = priceBasisUpperBound;
    }


    public BigDecimal getPriceValue() {
        return priceValue;
    }


    public void setPriceValue(BigDecimal priceValue) {
        this.priceValue = priceValue;
    }


    public boolean isDefaultEntry() {
        return defaultEntry;
    }


    public void setDefaultEntry(boolean defaultEntry) {
        this.defaultEntry = defaultEntry;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((priceBasisLowerBound == null) ? 0 : priceBasisLowerBound.hashCode());
        result = prime * result + ((priceBasisUpperBound == null) ? 0 : priceBasisUpperBound.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SlabBasedPriceEntry other = (SlabBasedPriceEntry) obj;
        if (priceBasisLowerBound == null) {
            if (other.priceBasisLowerBound != null)
                return false;
        } else if (!priceBasisLowerBound.equals(other.priceBasisLowerBound))
            return false;
        if (priceBasisUpperBound == null) {
            if (other.priceBasisUpperBound != null)
                return false;
        } else if (!priceBasisUpperBound.equals(other.priceBasisUpperBound))
            return false;
        return true;
    }


    public SlabBasedPriceEntry clone() throws CloneNotSupportedException {

        SlabBasedPriceEntry clone =  (SlabBasedPriceEntry) super.clone();

        return clone;
    }

}

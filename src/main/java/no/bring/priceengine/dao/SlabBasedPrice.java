package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


@Entity
@Table(name = "slabbasedprice", schema = "core")
@Cacheable(true)
public class SlabBasedPrice  implements Serializable, Cloneable{


    private static final long serialVersionUID = 6920255084042914393L;

    @Id
    @SequenceGenerator(sequenceName = "core.slabbasedprice_slabbasedprice_id_seq", name = "SlabBasedPriceIdSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SlabBasedPriceIdSeq")
    @Column(name = "slabbasedprice_id")
    private Long slabBasedPriceId;

//
//    @ManyToOne
    @JoinColumn(name = "price_id")
    private Price price;


    @Column(name = "slabbasis_tp_cd")
    private Long slabBasisTpCd;

    @Column(name = "default_price")
    private BigDecimal defaultPriceValue;


    @OneToMany(fetch=FetchType.LAZY, mappedBy="slabBasedPrice")
    @MapKey(name = "priceBasisLowerBound")
    @OrderBy("price_basis_lower_bound ASC")
    @JsonManagedReference
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Map<Long, SlabBasedPriceEntry> slabBasedPriceEntryMap1 = new HashMap<>();


    @Transient
    private TreeMap<Long, SlabBasedPriceEntry> slabBasedPriceEntryMap = null;



    public SlabBasedPrice() {

    }

    @PostLoad
    private void onLoad() {

        if (slabBasedPriceEntryMap1 != null && !slabBasedPriceEntryMap1.isEmpty())
            this.slabBasedPriceEntryMap = new TreeMap<Long, SlabBasedPriceEntry>(slabBasedPriceEntryMap1);

    }

    public Long getSlabBasedPriceId() {
        return slabBasedPriceId;
    }

    public void setSlabBasedPriceId(Long slabBasedPriceId) {
        this.slabBasedPriceId = slabBasedPriceId;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price priceId) {
        this.price = price;
    }

    public Long getSlabBasisTpCd() {
        return slabBasisTpCd;
    }

    public void setSlabBasisTpCd(Long slabBasisTpCd) {
        this.slabBasisTpCd = slabBasisTpCd;
    }

    public BigDecimal getDefaultPriceValue() {
        return defaultPriceValue;
    }

    public void setDefaultPriceValue(BigDecimal defaultPriceValue) {
        this.defaultPriceValue = defaultPriceValue;
    }

    public TreeMap<Long, SlabBasedPriceEntry> getSlabBasedPriceEntryMap() {
        return slabBasedPriceEntryMap;
    }

    public void setSlabBasedPriceEntryMap(TreeMap<Long, SlabBasedPriceEntry> slabBasedPriceEntryMap) {
        this.slabBasedPriceEntryMap = slabBasedPriceEntryMap;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((slabBasedPriceEntryMap1 == null) ? 0 : slabBasedPriceEntryMap1.hashCode());
        result = prime * result + ((slabBasisTpCd == null) ? 0 : slabBasisTpCd.hashCode());
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
        SlabBasedPrice other = (SlabBasedPrice) obj;
        if (slabBasedPriceEntryMap1 == null) {
            if (other.slabBasedPriceEntryMap1 != null)
                return false;
        } else if (!slabBasedPriceEntryMap1.equals(other.slabBasedPriceEntryMap1))
            return false;
        if (slabBasisTpCd == null) {
            if (other.slabBasisTpCd != null)
                return false;
        } else if (!slabBasisTpCd.equals(other.slabBasisTpCd))
            return false;
        return true;
    }

}

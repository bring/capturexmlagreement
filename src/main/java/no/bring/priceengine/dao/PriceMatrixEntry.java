package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;



@Entity
@Table(name = "pricematrixentry", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({

        @NamedQuery(name = "PriceMatrixEntry.findForPriceMatrixEntryByPriceMatrixId", query =
                "select pme from PriceMatrixEntry pme where pme.priceMatrix.priceMatrixId = :priceMatrixId order by zoneNumber, priceBasisLowerBound ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),

        }) })
public class PriceMatrixEntry {

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "price_basis_lower_bound")
    private Long priceBasisLowerBound;

    @Column(name = "price_basis_upper_bound")
    private Long priceBasisUpperBound;

    @ManyToOne
    @JoinColumn(name = "pricematrix_id")
    @JsonBackReference
    private PriceMatrix priceMatrix;

    @Id
    @SequenceGenerator(sequenceName = "core.pricematrixentry_pricematrixentry_id_seq", name = "PriceMatrixEntryIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PriceMatrixEntryIdSequence")
    @Column(name = "pricematrixentry_id")
    private Long priceMatrixEntryId;

    @Column(name = "zone_number")
    private Long zoneNumber;

    protected PriceMatrixEntry() {
    }

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (!(o instanceof PriceMatrixEntry)) {
            return false;
        }
        PriceMatrixEntry priceMatrixEntry = (PriceMatrixEntry) o;
        return Objects.equals(priceMatrixEntryId, priceMatrixEntry.priceMatrixEntryId)
                && Objects.equals(priceBasisLowerBound, priceMatrixEntry.priceBasisLowerBound)
                && Objects.equals(priceBasisUpperBound, priceMatrixEntry.priceBasisUpperBound);

    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getPriceBasisLowerBound() {
        return priceBasisLowerBound;
    }

    public Long getPriceBasisUpperBound() {
        return priceBasisUpperBound;
    }

    public PriceMatrix getPriceMatrix() {
        return priceMatrix;
    }

    public Long getPriceMatrixEntryId() {
        return priceMatrixEntryId;
    }

    public Long getZoneNumber() {
        return zoneNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priceMatrixEntryId, priceBasisLowerBound, priceBasisUpperBound);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setPriceBasisLowerBound(Long priceBasisLowerBound) {
        this.priceBasisLowerBound = priceBasisLowerBound;
    }

    public void setPriceBasisUpperBound(Long priceBasisUpperBound) {
        this.priceBasisUpperBound = priceBasisUpperBound;
    }

    public void setPriceMatrix(PriceMatrix priceMatrix) {
        this.priceMatrix = priceMatrix;
    }

    public void setPriceMatrixEntryId(Long priceMatrixEntryId) {
        this.priceMatrixEntryId = priceMatrixEntryId;
    }

    public void setZoneNumber(Long zoneNumber) {
        this.zoneNumber = zoneNumber;
    }

    @Override
    public String toString() {
        return "PriceMatrixEntry [priceMatrixEntryId=" + priceMatrixEntryId + ", priceBasisLowerBound="
                + priceBasisLowerBound + ", priceBasisUpperBound=" + priceBasisUpperBound + ", price=" + price
                + ", zoneNumber=" + zoneNumber + "]";
    }

}
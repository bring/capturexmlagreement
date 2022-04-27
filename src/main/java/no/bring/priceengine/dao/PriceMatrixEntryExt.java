package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "pricematrixentryext", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({

        @NamedQuery(name = "PriceMatrixEntryExt.findForPriceMatrixEntryExtByPriceMatrixId", query =
                "select pmee from PriceMatrixEntryExt pmee where pmee.priceMatrix.priceMatrixId = :priceMatrixId order by pmee.zoneNumber ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),

        }) })
public class PriceMatrixEntryExt {

    @Column(name = "additional_slab")
    private BigDecimal additionalSlab;

    @Column(name = "additional_slab_price")
    private BigDecimal additionalSlabPrice;

    @Column(name = "base_price")
    private double basePrice;

    @ManyToOne
    @JoinColumn(name = "pricematrix_id")
    @JsonBackReference
    private PriceMatrix priceMatrix;

    @Id
    @SequenceGenerator(sequenceName = "core.pricematrixentryext_pricematrixentryext_id_seq", name = "PriceMatrixEntryExtIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PriceMatrixEntryExtIdSequence")
    @Column(name = "pricematrixentryext_id")
    private long priceMatrixEntryExtId;

    @Column(name = "zone_number")
    private Long zoneNumber;

    protected PriceMatrixEntryExt() {
    }

    public BigDecimal getAdditionalSlab() {
        return additionalSlab;
    }

    public BigDecimal getAdditionalSlabPrice() {
        return additionalSlabPrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public BigDecimal getPrice(Long priceBasis) {

        double basePrice = this.getBasePrice();
        BigDecimal additionalSlab = this.getAdditionalSlab();
        BigDecimal additionalSlabPrice = this.getAdditionalSlabPrice();

        Long noOfSlabs = new BigDecimal(priceBasis - this.priceMatrix.getPriceMatrixMaxPriceBasisValue()).divide(additionalSlab).setScale(0, BigDecimal.ROUND_UP).longValue();

        BigDecimal additionalSlabTotalPrice =  additionalSlabPrice.multiply(BigDecimal.valueOf(noOfSlabs));
        BigDecimal price = additionalSlabTotalPrice.add(BigDecimal.valueOf(basePrice));
        return price;
    }

    public PriceMatrix getPriceMatrix() {
        return priceMatrix;
    }

    public long getPriceMatrixEntryExtId() {
        return priceMatrixEntryExtId;
    }

    public Long getZoneNumber() {
        return zoneNumber;
    }

    public void setAdditionalSlab(BigDecimal additionalSlab) {
        this.additionalSlab = additionalSlab;
    }

    public void setAdditionalSlabPrice(BigDecimal additionalSlabPrice) {
        this.additionalSlabPrice = additionalSlabPrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setPriceMatrix(PriceMatrix priceMatrix) {
        this.priceMatrix = priceMatrix;
    }

    public void setPriceMatrixEntryExtId(long priceMatrixEntryExtId) {
        this.priceMatrixEntryExtId = priceMatrixEntryExtId;
    }

    public void setZoneNumber(Long zoneNumber) {
        this.zoneNumber = zoneNumber;
    }

}
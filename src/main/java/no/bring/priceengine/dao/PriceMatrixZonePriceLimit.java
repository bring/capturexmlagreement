package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "pricematrixzonepricelimit", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
/*@NamedQueries({

	@NamedQuery(name = "PriceMatrixZonePriceLimit.findPriceMatrixZonePriceLimitByPriceMatrixId", query =
			"select pmzpl from PriceMatrixZonePriceLimit pmzpl where pmzpl.priceMatrix.priceMatrixId = :priceMatrixId order by pmee.zoneNumber ", hints = {
					@QueryHint(name = "org.hibernate.cacheable", value = "true"),

	}) })*/
public class PriceMatrixZonePriceLimit {


    @Id
    @SequenceGenerator(sequenceName = "core.pricematrixzonepricelimit_pricematrixzonepricelimit_id_seq", name = "PriceMatrixZonePriceLimitIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PriceMatrixZonePriceLimitIdSequence")
    @Column(name = "pricematrixzonepricelimit_id")
    private long PriceMatrixZonePriceLimitId;


    @ManyToOne
    @JoinColumn(name = "pricematrix_id")
    @JsonBackReference
    private PriceMatrix priceMatrix;


    @Column(name = "zone_number")
    private Long zoneNumber;

    @Column(name = "min_price")
    private BigDecimal minPrice;


    @Column(name = "max_price")
    private BigDecimal maxPrice;


    protected PriceMatrixZonePriceLimit() {
    }


    public long getPriceMatrixZonePriceLimitId() {
        return PriceMatrixZonePriceLimitId;
    }


    public void setPriceMatrixZonePriceLimitId(long priceMatrixZonePriceLimitId) {
        PriceMatrixZonePriceLimitId = priceMatrixZonePriceLimitId;
    }


    public PriceMatrix getPriceMatrix() {
        return priceMatrix;
    }


    public void setPriceMatrix(PriceMatrix priceMatrix) {
        this.priceMatrix = priceMatrix;
    }


    public Long getZoneNumber() {
        return zoneNumber;
    }


    public void setZoneNumber(Long zoneNumber) {
        this.zoneNumber = zoneNumber;
    }


    public BigDecimal getMinPrice() {
        return minPrice;
    }


    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }


    public BigDecimal getMaxPrice() {
        return maxPrice;
    }


    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }



}
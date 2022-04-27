package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "pricematrixprice", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({

        @NamedQuery(name = "PriceMatrixPrice.findForPriceMatrixByPriceId", query =
                " select pmp from PriceMatrixPrice pmp where pmp.price.priceId = :priceId ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),

        }),
        @NamedQuery(name = "PriceMatrixPrice.findForPriceMatrixByPriceIdAndDate", query =
                " select pmp from PriceMatrixPrice pmp where pmp.price.priceId = :priceId and :date between pmp.startDt and pmp.endDt ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),

        })

})
public class PriceMatrixPrice implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2950185285956002502L;

    @XmlTransient
    @JsonIgnore
    @Transient
    private ApplicabilityCriteria applicabilityCriteria;


    @XmlTransient
    @Transient
    private String applicabilityCriteriaJSONString;

    @XmlTransient
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicabilitycriteria_id" )
    private ApplicabilityCriteriaEntity applicabilityCriteriaEntity;



    @ManyToOne
    @JoinColumn(name = "price_id")
    @JsonBackReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Price price;

    @ManyToOne
    @JoinColumn(name = "pricematrix_id")
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private PriceMatrix priceMatrix;

    @Id
    @SequenceGenerator(sequenceName = "core.pricematrixprice_pricematrixprice_id_seq", name = "PriceMatrixPriceIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PriceMatrixPriceIdSequence")
    @Column(name = "pricematrixprice_id")
    private long priceMatrixPriceId;


    @Column(name = "start_dt")
    @NotNull
    LocalDate startDt;


    @Column(name = "end_dt")
    @NotNull
    LocalDate endDt;

    @Column(name = "min_price_after_discount")
    private BigDecimal minPriceAfterDiscount;


    @XmlTransient
    @JsonIgnore
    public ApplicabilityCriteria getApplicabilityCriteria() {
        return applicabilityCriteria;
    }

    public String getApplicabilityCriteriaJSONString() {
        return applicabilityCriteriaJSONString;
    }

    public Price getPrice() {
        return price;
    }

    public PriceMatrix getPriceMatrix() {
        return priceMatrix;
    }

    public long getPriceMatrixPriceId() {
        return priceMatrixPriceId;
    }

    public void setApplicabilityCriteria(ApplicabilityCriteria applicabilityCriteria) {
        this.applicabilityCriteria = applicabilityCriteria;
    }

    public void setApplicabilityCriteriaJSONString(String applicabilityCriteriaJSONString) {
        this.applicabilityCriteriaJSONString = applicabilityCriteriaJSONString;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setPriceMatrix(PriceMatrix priceMatrix) {
        this.priceMatrix = priceMatrix;
    }

    public void setPriceMatrixPriceId(long priceMatrixPriceId) {
        this.priceMatrixPriceId = priceMatrixPriceId;
    }

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

    public BigDecimal getMinPriceAfterDiscount() {
        return minPriceAfterDiscount;
    }

    public void setMinPriceAfterDiscount(BigDecimal minPriceAfterDiscount) {
        this.minPriceAfterDiscount = minPriceAfterDiscount;
    }

    @PostLoad
    private void onLoad() {
        if (this.applicabilityCriteriaEntity != null ) {
            this.applicabilityCriteriaJSONString = this.applicabilityCriteriaEntity.getApplicabilityCriteriaJSONString();
        }
    }

}
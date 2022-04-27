package no.bring.priceengine.dao;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pricematrixzonetable", schema = "core")
@Cacheable(true)
@NamedQueries({

        @NamedQuery(name = "PriceMatrixZoneTable.findPriceMatrixZoneTableByPricematrixId", query =
                " select pmzt from PriceMatrixZoneTable pmzt where pmzt.priceMatrixId = :priceMatrixId order by pmzt.zoneTableId ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),

        }) })
public class PriceMatrixZoneTable {

    @Id
    @SequenceGenerator(sequenceName = "core.pricematrixzonetable_pricematrixzonetable_id_seq", name = "priceMatrixZoneTableIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priceMatrixZoneTableIdSequence")
    @Column(name = "pricematrixzonetable_id")
    private Long priceMatrixZoneTableId;

    @Column(name = "pricematrix_id")
    private Long priceMatrixId;

    @Column(name = "zonetable_id")
    private Long zoneTableId;

    @Column(name = "start_dt")
    private LocalDate startDt;

    @Column(name = "end_dt")
    private LocalDate endDt;


    @Column(name = "zonetable_tp_cd")
    private Integer zonetableTpCd;


    public Long getPriceMatrixZoneTableId() {
        return priceMatrixZoneTableId;
    }

    public void setPriceMatrixZoneTableId(Long priceMatrixZoneTableId) {
        this.priceMatrixZoneTableId = priceMatrixZoneTableId;
    }

    public Long getPriceMatrixId() {
        return priceMatrixId;
    }

    public void setPriceMatrixId(Long priceMatrixId) {
        this.priceMatrixId = priceMatrixId;
    }

    public Long getZoneTableId() {
        return zoneTableId;
    }

    public void setZoneTableId(Long zoneTableId) {
        this.zoneTableId = zoneTableId;
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

    public Integer getZonetableTpCd() {
        return zonetableTpCd;
    }

    public void setZonetableTpCd(Integer zonetableTpCd) {
        this.zonetableTpCd = zonetableTpCd;
    }

}
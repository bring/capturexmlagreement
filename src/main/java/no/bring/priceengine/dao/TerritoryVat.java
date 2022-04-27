package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "territoryvat", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class TerritoryVat {

    @Column(name = "end_dt")
    @JsonIgnore
    private LocalDate endDt;

    @Column(name = "start_dt")
    @NotNull
    @JsonIgnore
    private LocalDate startDt;

    @ManyToOne
    @JoinColumn(name = "territory_tp_cd")
    @JsonManagedReference
    private Territory territory;

    @Id
    @SequenceGenerator(sequenceName = "core.territoryvat_territoryvat_id_seq", name = "TerritoryVatIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TerritoryVatIdSequence")
    @Column(name = "territoryvat_id")
    private long territoryVatId;

    @ManyToOne
    @JoinColumn(name = "vat_code")
    @JsonBackReference
    private Vat vat;

    public TerritoryVat() {
    }

    public LocalDate getEndDt() {
        return endDt;
    }

    public LocalDate getStartDt() {
        return startDt;
    }

    public Territory getTerritory() {
        return territory;
    }

    public long getTerritoryVatId() {
        return territoryVatId;
    }

    public Vat getVat() {
        return vat;
    }

    public void setEndDt(LocalDate endDt) {
        this.endDt = endDt;
    }

    public void setStartDt(LocalDate startDt) {
        this.startDt = startDt;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public void setTerritoryVatId(long territoryVatId) {
        this.territoryVatId = territoryVatId;
    }

    public void setVat(Vat vat) {
        this.vat = vat;
    }

}

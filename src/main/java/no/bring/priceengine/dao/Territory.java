package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cdterritorytp", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Territory {



    @Column(name = "name")
    private String name;

    @Id
    @Column(name = "territory_tp_cd")
    private String territoryTpCd;

    @OneToMany(mappedBy = "territory", fetch = FetchType.EAGER)
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<TerritoryVat> territoryVats = new HashSet<>();

    public Territory() {
        super();
    }


    public String getName() {
        return name;
    }

    public String getTerritoryTpCd() {
        return territoryTpCd;
    }

    public Set<TerritoryVat> getTerritoryVats() {
        return territoryVats;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setTerritoryTpCd(String territoryTpCd) {
        this.territoryTpCd = territoryTpCd;
    }

    public void setTerritoryVats(Set<TerritoryVat> territoryVats) {
        this.territoryVats = territoryVats;
    }

}

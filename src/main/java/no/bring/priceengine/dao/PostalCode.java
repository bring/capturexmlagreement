package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "postalcode", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({

        @NamedQuery(name = "PostalCode.findByPostalCode", query = " select p from PostalCode p   "
                + " left join fetch p.territory t  " + " left join fetch t.territoryVats tvs "
                + " left join fetch tvs.vat v " + " where p.postalCode= :postalCode  ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),

        })

})
public class PostalCode implements Serializable {

    @Column(name = "country_tp_cd")
    private Integer countryCode;

    @Column(name = "postalcode")
    private String postalCode;

    @Id
    @SequenceGenerator(sequenceName = "core.postalcode_postalcode_id_seq", name = "PostalCodeIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PostalCodeIdSequence")
    @Column(name = "postalcode_id")
    private Long postalCodeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "territory_tp_cd")
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Territory territory;

    public PostalCode() {
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Long getPostalCodeId() {
        return postalCodeId;
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPostalCodeId(Long postalCodeId) {
        this.postalCodeId = postalCodeId;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }


}

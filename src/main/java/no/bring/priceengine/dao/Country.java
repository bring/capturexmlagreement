package no.bring.priceengine.dao;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name = "cdcountrytp" , schema = "core")
@NamedQueries({
        @NamedQuery(name = "Country.findByCountryCode",
                query = "SELECT c  FROM Country  c WHERE c.countryCode = :countryCode" )
})
public class Country implements Serializable {

    private static final long serialVersionUID = -6588916782102501677L;

    public Country() {

    }

    public Country(String countryCode) {
            this.countryCode = countryCode;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name = "country_tp_cd" )
    private Integer countryTpCd;

    @Column( name = "country_code" )
    private String countryCode;

    @Column( name = "country_name" )
    private String countryName;

    @Column( name = "currency_code" )
    private String currencyCode;

    public Integer getCountryTpCd() {
        return countryTpCd;
    }

    public void setCountryTpCd(Integer countryTpCd) {
        this.countryTpCd = countryTpCd;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }




}

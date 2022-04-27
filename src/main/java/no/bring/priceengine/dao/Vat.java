package no.bring.priceengine.dao;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;


@Entity
@Table(name = "vat", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Vat {


    @Id
    @Column(name = "vat_code")
    private Long vatCode;

    @Column(name = "vat_description")
    private String vatDescription;

    @Column(name = "vat_percent")
    private BigDecimal vatPercent;

    public Vat() {
        super();
    }


    @XmlElement(name = "VatCode")
    @JacksonXmlProperty(localName = "VatCode")
    public Long getVatCode() {
        return vatCode;
    }


    @XmlElement(name = "VatDescription")
    @JacksonXmlProperty(localName = "VatDescription")
    public String getVatDescription() {
        return vatDescription;
    }

    @XmlElement(name = "VatPercent")
    @JacksonXmlProperty(localName = "VatPercent")
    public BigDecimal getVatPercent() {
        return vatPercent;
    }

    public void setVatCode(Long vatCode) {
        this.vatCode = vatCode;
    }

    public void setVatDescription(String vatDescription) {
        this.vatDescription = vatDescription;
    }

    public void setVatPercent(BigDecimal vatPercent) {
        this.vatPercent = vatPercent;
    }
}

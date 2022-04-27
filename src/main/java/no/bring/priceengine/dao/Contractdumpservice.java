package no.bring.priceengine.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "contractdumpservice",schema="core")
public class Contractdumpservice implements Serializable , Comparable<Contractdumpservice>{

    @Id
    @SequenceGenerator(sequenceName = "core.contractdumpservice_serialid", name = "serialidSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serialidSeq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "organization_number")
    private String organizationNumber;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "customer_name")
    private String customerName;

//    @Column(name = "Description")
//    private String Description;

    @Column(name = "Div")
    private Integer Div;

    @Column(name = "Artikelgrupp")
    private Integer Artikelgrupp;

    @Column(name = "Statgrupp")
    private String StatGrupp;

    @Column(name = "Prodno")
    private Integer ProdNo;

    @Column(name = "Proddescription")
    private String ProdDescr;

    @Column(name = "Routefrom")
    private String RouteFrom;

    @Column(name = "Routeto")
    private String RouteTo;

    @Column(name = "routetype")
    private String routetype;

    @Column(name = "startdate")
    private Date FromDate;

    @Column(name = "Todate")
    private Date ToDate;

    @Column(name = "BasePrice")
    private Double basePrice;

    @Column(name = "Currency")
    private String Curr;

    @Column(name = "Prum")
    private String PrUM;

    @Column(name = "dsclimcd")
    private Integer DscLimCd;

    @Column(name = "kgtill")
    private String KgTill;

    @Column(name = "filecountry")
    private String fileCountry;

    @Column(name = "disclmtfrom")
    private Double DiscLmtFrom;

    @Column(name = "Price")
    private Double price;

    @Column(name = "Adsc")
    private Integer ADsc;

    @Column(name = "Updated")
    private boolean updated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public String getDescription() {
//        return Description;
//    }
//
//    public void setDescription(String description) {
//        Description = description;
//    }

    public Integer getDiv() {
        return Div;
    }

    public void setDiv(Integer div) {
        Div = div;
    }

    public Integer getArtikelgrupp() {
        return Artikelgrupp;
    }

    public void setArtikelgrupp(Integer artikelgrupp) {
        Artikelgrupp = artikelgrupp;
    }

    public String getStatGrupp() {
        return StatGrupp;
    }

    public void setStatGrupp(String statGrupp) {
        StatGrupp = statGrupp;
    }

    public Integer getProdNo() {
        return ProdNo;
    }

    public void setProdNo(Integer prodNo) {
        ProdNo = prodNo;
    }

    public String getProdDescr() {
        return ProdDescr;
    }

    public void setProdDescr(String prodDescr) {
        ProdDescr = prodDescr;
    }

    public String getRouteFrom() {
        return RouteFrom;
    }

    public void setRouteFrom(String routeFrom) {
        RouteFrom = routeFrom;
    }

    public String getRouteTo() {
        return RouteTo;
    }

    public void setRouteTo(String routeTo) {
        RouteTo = routeTo;
    }

    public Date getFromDate() {
        return FromDate;
    }

    public void setFromDate(Date fromDate) {
        FromDate = fromDate;
    }

    public Date getToDate() {
        return ToDate;
    }

    public void setToDate(Date toDate) {
        ToDate = toDate;
    }



    public String getCurr() {
        return Curr;
    }

    public void setCurr(String curr) {
        Curr = curr;
    }

    public String getPrUM() {
        return PrUM;
    }

    public void setPrUM(String prUM) {
        PrUM = prUM;
    }

    public Integer getDscLimCd() {
        return DscLimCd;
    }

    public void setDscLimCd(Integer dscLimCd) {
        DscLimCd = dscLimCd;
    }

    public String getKgTill() {
        return KgTill;
    }

    public void setKgTill(String kgTill) {
        KgTill = kgTill;
    }

    public Double getDiscLmtFrom() {
        return DiscLmtFrom;
    }

    public void setDiscLmtFrom(Double discLmtFrom) {
        DiscLmtFrom = discLmtFrom;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    public Integer getADsc() {
        return ADsc;
    }

    public void setADsc(Integer aDsc) {
        ADsc = aDsc;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getFileCountry() {
        return fileCountry;
    }

    public void setFileCountry(String fileCountry) {
        this.fileCountry = fileCountry;
    }

    //    @Override
//    public String toString() {
//        return "Agreemen [id=" + id + ", PrcLst=" + PrcLst + ", Description=" + Description + ", ProdDescr=" + ProdDescr + "]";
//    }

    @Override
    public String toString() {
        return "DiscLmtForm [id=" + DiscLmtFrom + "]";
    }

    @Override
    public int compareTo(Contractdumpservice contractdumpservice) {
        if(contractdumpservice.getDiscLmtFrom()!=null)
            return this.getDiscLmtFrom().compareTo(contractdumpservice.getDiscLmtFrom());
        else
            return 0;
    }

    public String getOrganizationNumber() {
        return organizationNumber;
    }

    public void setOrganizationNumber(String organizationNumber) {
        this.organizationNumber = organizationNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRouteType() {
        return routetype;
    }

    public void setRouteType(String routeType) {
        this.routetype = routeType;
    }
}

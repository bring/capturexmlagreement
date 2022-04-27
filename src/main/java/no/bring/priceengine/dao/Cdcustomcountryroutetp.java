package no.bring.priceengine.dao;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "cdcustomcountryroutetp", schema = "core")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Cdcustomcountryroutetp implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "core.cdcustomcountryroutetp_serialid", name = "serialidSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serialidSeq")
    @Column(name = "route_id")
    private Integer routeId;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "country_id")
    private Integer countryId;

    @Column(name = "from_postcode")
    private String fromPostcode;

    @Column(name = "to_postcode")
    private String toPostcode;

    @Column(name = "start_dt")
    LocalDate startDt;

    @Column(name = "end_dt")
    LocalDate endDt;

    @Column(name = "created_by_user")
    private String createdByUser;

    @Column(name = "created_dt")
    LocalDate createdDt;

    @Column(name = "last_update_dt")
    LocalDate lastUpdateDt;

    @Column(name = "last_update_user")
    private String lastUpdateUser;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getFromPostcode() {
        return fromPostcode;
    }

    public void setFromPostcode(String fromPostcode) {
        this.fromPostcode = fromPostcode;
    }

    public String getToPostcode() {
        return toPostcode;
    }

    public void setToPostcode(String toPostcode) {
        this.toPostcode = toPostcode;
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

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public LocalDate getLastUpdateDt() {
        return lastUpdateDt;
    }

    public void setLastUpdateDt(LocalDate lastUpdateDt) {
        this.lastUpdateDt = lastUpdateDt;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public LocalDate getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(LocalDate createdDt) {
        this.createdDt = createdDt;
    }
}

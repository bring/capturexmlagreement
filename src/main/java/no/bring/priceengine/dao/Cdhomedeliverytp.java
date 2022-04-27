package no.bring.priceengine.dao;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


// Previous name -  Cdcustomzonetp Renamed by Uma in June 2021
@Entity
@Table(name = "cdhomedeliverytp", schema = "core")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Cdhomedeliverytp implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "core.cdcustomzonetp_serialid", name = "serialidSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serialidSeq")
    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "created_by_user")
    private String createdByUser;

    @Column(name = "last_update_dt")
    LocalDate lastUpdateDt;

    @Column(name = "last_update_user")
    private String lastUpdateUser;

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
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
}

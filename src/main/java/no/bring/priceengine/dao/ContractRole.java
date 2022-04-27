package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "contractrole", schema = "core")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Cacheable(true)
public class ContractRole  implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 5246455661849514115L;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "contractcomponent_id")
    @JsonBackReference
    @Where(clause = "contractComponentStTpCd = '1'")
    private ContractComponent contractComponent;

    @Id
    @SequenceGenerator(sequenceName = "core.contractrole_contractrole_id_seq", name = "ContractRoleIdSeq")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ContractroleIdSeq")
    @Column(name = "contractrole_id")
    private Long contractRoleId;

    @Column(name = "contractrole_st_tp_cd")
    private Integer contractRoleStTpCd;

    @Column(name = "contractrole_tp_cd")
    private Integer contractRoleTpCd;

    @Column(name = "created_dt")
    LocalDate createdDt;

    @Column(name = "agreement_currency")
    private String agreementCurrency;

    @Column(name = "end_dt")
    LocalDate endDt;

    @Column(name = "last_update_dt")
    LocalDate lastUpdateDt;

    @Column(name = "last_update_tx_id")
    private Long lastUpdateTxId;

    @Column(name = "last_update_user")
    private String lastUpdateUser;

    @Column(name = "created_by_user")
    private String createdByUser;

    @ManyToOne(cascade =CascadeType.ALL , fetch = FetchType.EAGER)
    @JoinColumn(name = "party_id")
    @JsonBackReference
    private Party party;

    @Column(name = "party_source_system_record_pk")
    private String partySourceSystemRecordPk;

    @Column(name = "start_dt")
    @NotNull
    LocalDate startDt;

//    protected ContractRole() {
//    }
    public ContractRole() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContractRole other = (ContractRole) obj;
        if (contractRoleId == null) {
            if (other.contractRoleId != null)
                return false;
        } else if (!contractRoleId.equals(other.contractRoleId))
            return false;
        if (contractRoleStTpCd == null) {
            if (other.contractRoleStTpCd != null)
                return false;
        } else if (!contractRoleStTpCd.equals(other.contractRoleStTpCd))
            return false;
        if (contractRoleTpCd == null) {
            if (other.contractRoleTpCd != null)
                return false;
        } else if (!contractRoleTpCd.equals(other.contractRoleTpCd))
            return false;
        if (endDt == null) {
            if (other.endDt != null)
                return false;
        } else if (!endDt.equals(other.endDt))
            return false;
        if (partySourceSystemRecordPk == null) {
            if (other.partySourceSystemRecordPk != null)
                return false;
        } else if (!partySourceSystemRecordPk.equals(other.partySourceSystemRecordPk))
            return false;
        if (startDt == null) {
            if (other.startDt != null)
                return false;
        } else if (!startDt.equals(other.startDt))
            return false;
        return true;
    }

    public ContractComponent getContractComponent() {
        return contractComponent;
    }

    public Long getContractRoleId() {
        return contractRoleId;
    }

    public Integer getContractRoleStTpCd() {
        return contractRoleStTpCd;
    }

    public Integer getContractRoleTpCd() {
        return contractRoleTpCd;
    }

    public LocalDate getCreatedDt() {
        return createdDt;
    }

    public LocalDate getEndDt() {
        return endDt;
    }

    public LocalDate getLastUpdateDt() {
        return lastUpdateDt;
    }

    public Long getLastUpdateTxId() {
        return lastUpdateTxId;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public Party getParty() {
        return party;
    }

    public String getPartySourceSystemRecordPk() {
        return partySourceSystemRecordPk;
    }

    public LocalDate getStartDt() {
        return startDt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contractRoleId == null) ? 0 : contractRoleId.hashCode());
        result = prime * result + ((contractRoleStTpCd == null) ? 0 : contractRoleStTpCd.hashCode());
        result = prime * result + ((contractRoleTpCd == null) ? 0 : contractRoleTpCd.hashCode());
        result = prime * result + ((endDt == null) ? 0 : endDt.hashCode());
        result = prime * result + ((partySourceSystemRecordPk == null) ? 0 : partySourceSystemRecordPk.hashCode());
        result = prime * result + ((startDt == null) ? 0 : startDt.hashCode());
        return result;
    }

    public void setContractComponent(ContractComponent contractComponent) {
        this.contractComponent = contractComponent;
    }

    public void setContractRoleId(Long contractRoleId) {
        this.contractRoleId = contractRoleId;
    }

    public void setContractRoleStTpCd(Integer contractRoleStTpCd) {
        this.contractRoleStTpCd = contractRoleStTpCd;
    }

    public void setContractRoleTpCd(Integer contractRoleTpCd) {
        this.contractRoleTpCd = contractRoleTpCd;
    }

    public void setCreatedDt(LocalDate createdDt) {
        this.createdDt = createdDt;
    }

    public void setEndDt(LocalDate endDt) {
        this.endDt = endDt;
    }

    public void setLastUpdateDt(LocalDate lastUpdateDt) {
        this.lastUpdateDt = lastUpdateDt;
    }

    public void setLastUpdateTxId(Long lastUpdateTxId) {
        this.lastUpdateTxId = lastUpdateTxId;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setPartySourceSystemRecordPk(String partySourceSystemRecordPk) {
        this.partySourceSystemRecordPk = partySourceSystemRecordPk;
    }

    public void setStartDt(LocalDate startDt) {
        this.startDt = startDt;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public String getAgreementCurrency() {
        return agreementCurrency;
    }

    public void setAgreementCurrency(String agreementCurrency) {
        this.agreementCurrency = agreementCurrency;
    }
}

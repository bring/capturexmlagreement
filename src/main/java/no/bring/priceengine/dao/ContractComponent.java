package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contractcomponent", schema = "core")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ContractComponent implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2432508827789648926L;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "contract_id")
    @JsonBackReference
    private Contract contract;

    @Id
    @SequenceGenerator(sequenceName = "core.contractcomponent_contractcomponent_id_seq", name = "ContractComponentIdSeq")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ContractcomponentIdSeq")
    @Column(name = "contractcomponent_id")
    private Long contractComponentId;


//    @Id
//    @SequenceGenerator(sequenceName = "core.contract_contract_id_seq", name = "ContractIdSeq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ContractIdSeq")
//    @Column(name = "contract_id")
//    private Long contractId;



//    @Column(name = "contract_id")
//    private Long contractId;

    @Column(name = "contractcomponent_tp_cd")
    private Long contractComponentTpCd;

    @OneToMany(mappedBy = "contractComponent", cascade = { CascadeType.ALL })
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<ContractRole> contractRolesSet = new HashSet<>();

    @Column(name = "created_dt")
    LocalDate createdDt;

    @Column(name = "last_update_dt")
    LocalDate lastUpdateDt;

    @Column(name = "last_update_tx_id")
    private Long lastUpdateTxId;

    @Column(name = "last_update_user")
    private String lastUpdateUser;

    @Column(name = "created_by_user")
    private String createdByUser;


    @Column(name = "source_system_id")
    private Integer sourceSystemId;

    @Column(name = "source_system_record_pk")
    private String sourceSystemRecordPk;


    @Column(name = "start_dt")
    @NotNull
    LocalDate startDt;


    @Column(name = "end_dt")
    LocalDate endDt;

    @Column(name = "contractcomponent_st_tp_cd")
    private Integer contractComponentStTpCd;


    @OneToMany(mappedBy = "contractComponent", cascade = { CascadeType.ALL })
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<ContractPrice> contractPriceSet = new HashSet<>();



    @OneToMany(mappedBy = "contractComponent", cascade = { CascadeType.ALL })
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<CustomProductAttribute> customProductAttributeSet = new HashSet<>();

    public ContractComponent() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContractComponent other = (ContractComponent) obj;
        if (contract == null) {
            if (other.contract != null)
                return false;
        } else if (!contract.equals(other.contract))
            return false;
        if (contractComponentId == null) {
            if (other.contractComponentId != null)
                return false;
        } else if (!contractComponentId.equals(other.contractComponentId))
            return false;

        if (contractComponentTpCd == null) {
            if (other.contractComponentTpCd != null)
                return false;
        } else if (!contractComponentTpCd.equals(other.contractComponentTpCd))
            return false;
        if (sourceSystemId == null) {
            if (other.sourceSystemId != null)
                return false;
        } else if (!sourceSystemId.equals(other.sourceSystemId))
            return false;
        if (sourceSystemRecordPk == null) {
            if (other.sourceSystemRecordPk != null)
                return false;
        } else if (!sourceSystemRecordPk.equals(other.sourceSystemRecordPk))
            return false;
        return true;
    }

//    public Long getContractId() {
//        return contractId;
//    }
//
//    public void setContractId(Long contractId) {
//        this.contractId = contractId;
//    }

    public Contract getContract() {
        return contract;
    }

    public Long getContractComponentId() {
        return contractComponentId;
    }

    public Long getContractComponentTpCd() {
        return contractComponentTpCd;
    }

    public Set<ContractRole> getContractRolesSet() {
        return contractRolesSet;
    }

    public LocalDate getCreatedDt() {
        return createdDt;
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



    public Integer getSourceSystemId() {
        return sourceSystemId;
    }

    public String getSourceSystemRecordPk() {
        return sourceSystemRecordPk;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contract == null) ? 0 : contract.hashCode());
        result = prime * result + ((contractComponentId == null) ? 0 : contractComponentId.hashCode());
        result = prime * result + ((contractComponentTpCd == null) ? 0 : contractComponentTpCd.hashCode());
        result = prime * result + ((sourceSystemId == null) ? 0 : sourceSystemId.hashCode());
        result = prime * result + ((sourceSystemRecordPk == null) ? 0 : sourceSystemRecordPk.hashCode());
        return result;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public void setContractComponentId(Long contractComponentId) {
        this.contractComponentId = contractComponentId;
    }

    public void setContractComponentTpCd(Long contractComponentTpCd) {
        this.contractComponentTpCd = contractComponentTpCd;
    }

    public void setContractRolesSet(Set<ContractRole> contractRolesSet) {
        this.contractRolesSet = contractRolesSet;
    }

    public void setCreatedDt(LocalDate createdDt) {
        this.createdDt = createdDt;
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



    public void setSourceSystemId(Integer sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public void setSourceSystemRecordPk(String sourceSystemRecordPk) {
        this.sourceSystemRecordPk = sourceSystemRecordPk;
    }

    public Integer getContractComponentStTpCd() {
        return contractComponentStTpCd;
    }

    public void setContractComponentStTpCd(Integer contractComponentStTpCd) {
        this.contractComponentStTpCd = contractComponentStTpCd;
    }

    public Set<ContractPrice> getContractPriceSet() {
        return contractPriceSet;
    }

    public void setContractPriceSet(Set<ContractPrice> contractPriceSet) {
        this.contractPriceSet = contractPriceSet;
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

    public Set<CustomProductAttribute> getCustomProductAttributeSet() {
        return customProductAttributeSet;
    }

    public void setCustomProductAttributeSet(Set<CustomProductAttribute> customProductAttributeSet) {
        this.customProductAttributeSet = customProductAttributeSet;
    }

}

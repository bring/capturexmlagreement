package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//@NamedQueries({
//       @NamedQuery(name = "Contract.insertContract",
//                query = "INSERT INTO Contract (contractDescription, createdDt,lastUpdateTxId, createdByUser, isShellAgreement)  (?,?,?,?,?) " )
//})
@Entity
@Table(name = "contract", schema = "core")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Contract implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 9104155109394521004L;

    @OneToMany(fetch = FetchType.EAGER ,mappedBy = "contract", cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<ContractComponent> contractComponents = new HashSet<>();

    /*	@Column(name = "contract_description")
        private String contractDescription;
    */
    @Id
    @SequenceGenerator(sequenceName = "core.contract_contract_id_seq", name = "ContractIdSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ContractIdSeq")
    @Column(name = "contract_id")
    private Long contractId;

/*	@OneToMany(mappedBy = "contract", cascade = { CascadeType.ALL })
	@JsonBackReference
	private Set<ContractProductRelationship> contractProductRelationships = new HashSet<>();*/

/*	@Column(name = "contract_st_tp_cd")
	private Long contractStTpCd;*/

    @Column(name = "created_dt")
    LocalDate createdDt;

//	@Column(name = "end_dt")
//	LocalDate endDt;

    @Column(name = "last_update_dt")
    LocalDate lastUpdateDt;

    @Column(name = "last_update_tx_id")
    private Long lastUpdateTxId;

    @Column(name = "last_update_user")
    private String lastUpdateUser;

    @Column(name = "agg_agreement_name")
    private String agreementName;

    @Column(name = "sign_dt")
    LocalDateTime signDt;

    @Column(name = "source_system_id")
    private Integer sourceSystemId;

    @Column(name = "source_system_record_pk")
    private String sourceSystemRecordPk;

    @Column(name = "created_by_user")
    private String createdByUser;

    @Column(name = "contract_description")
    private String contractDescription;

    @Column(name = "is_shell_agreement")
    private Boolean isShellAgreement;

    /*@Column(name = "start_dt")
    @NotNull
    LocalDate startDt;
*/
    public Contract() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Contract other = (Contract) obj;

        if (contractId == null) {
            if (other.contractId != null)
                return false;
        } else if (!contractId.equals(other.contractId))
            return false;

        if (createdDt == null) {
            if (other.createdDt != null)
                return false;
        }

        if (signDt == null) {
            if (other.signDt != null)
                return false;
        } else if (!signDt.equals(other.signDt))
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

    public Set<ContractComponent> getContractComponents() {
        return contractComponents;
    }

	public String getContractDescription() {
		return contractDescription;
	}

    public Long getContractId() {
        return contractId;
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

    public LocalDateTime getSignDt() {
        return signDt;
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
        //	result = prime * result + ((contractDescription == null) ? 0 : contractDescription.hashCode());
        result = prime * result + ((contractId == null) ? 0 : contractId.hashCode());
        result = prime * result + ((signDt == null) ? 0 : signDt.hashCode());
        result = prime * result + ((sourceSystemId == null) ? 0 : sourceSystemId.hashCode());
        result = prime * result + ((sourceSystemRecordPk == null) ? 0 : sourceSystemRecordPk.hashCode());
        return result;
    }

    public void setContractComponents(Set<ContractComponent> contractComponents) {
        this.contractComponents = contractComponents;
    }

	public void setContractDescription(String contractDescription) {
		this.contractDescription = contractDescription;
	}

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public void setSignDt(LocalDateTime signDt) {
        this.signDt = signDt;
    }

    public void setSourceSystemId(Integer sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public void setSourceSystemRecordPk(String sourceSystemRecordPk) {
        this.sourceSystemRecordPk = sourceSystemRecordPk;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

//    public LocalDate getEndDt() {
//        return endDt;
//    }
//
//    public void setEndDt(LocalDate endDt) {
//        this.endDt = endDt;
//    }


    public String getAgreementName() {
        return agreementName;
    }

    public void setAgreementName(String agreementName) {
        this.agreementName = agreementName;
    }

    public Boolean getShellAgreement() {
        return isShellAgreement;
    }

    public void setShellAgreement(Boolean shellAgreement) {
        isShellAgreement = shellAgreement;
    }
}

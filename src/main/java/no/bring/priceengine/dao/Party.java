package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "party", schema = "core")

@NamedQueries({
        @NamedQuery(name = "Party.findByName", query = "select partyId,partyName from Party p where p.partyName= :partyName"),
        @NamedQuery(name = "Party.findBySourceSystemRecordPk", query = "select p from Party p where p.sourceSystemRecordPk= :sourceSystemRecordPk  ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Party {

    @Transient
    @JsonBackReference
    private Set<Party> childrenParties = new HashSet<>();

    @OneToMany(mappedBy = "party", cascade = { CascadeType.ALL })
    @JsonBackReference
    private Set<ContractRole> contractRolesSet = new HashSet<>();

    @Column(name = "created_dt")
    LocalDate createdDt;

    @Column(name = "last_update_dt")
    LocalDate lastUpdateDt;

    @Column(name = "last_update_tx_id")
    private Long lastUpdateTxId;

    @Column(name = "parent_party_id")
    private Long parentPartyId;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "last_update_user")
    private String lastUpdateUser;

    @Column(name = "created_by_user")
    private String createdByUser;

    @Transient
    @JsonBackReference
    private Party parentParty;

    @Column(name = "parent_source_system_record_pk")
    private String parentSourceSystemRecordPk;

    @Id
    @SequenceGenerator(sequenceName = "core.party_party_id_seq", name = "PartyIdSequence")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PartyIdSequence")
    @Column(name = "party_id")
    private Long partyId;

    @Column(name = "party_level_tp_cd")
    @NaturalId
    private Long partyLevelTpCd;

    @Column(name = "party_name")
    private String partyName;

    @Column(name = "source_system_id")
    private Long sourceSystemId;

    @Column(name = "source_system_record_pk")
    @NaturalId
    private String sourceSystemRecordPk;

//    protected Party() {
//    }

    public Party() {
    }

    public Party(Long partyId, String partyName) {
        this.partyId = partyId;
        this.partyName = partyName;

    }

    public Set<Party> getChildrenParties() {
        return childrenParties;
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

    public Party getParentParty() {
        return parentParty;
    }

    public String getParentSourceSystemRecordPk() {
        return parentSourceSystemRecordPk;
    }

    public Long getPartyId() {
        return partyId;
    }

    public Long getPartyLevelTpCd() {
        return partyLevelTpCd;
    }

    public String getPartyName() {
        return partyName;
    }

    public Long getSourceSystemId() {
        return sourceSystemId;
    }

    public String getSourceSystemRecordPk() {
        return sourceSystemRecordPk;
    }

    public void setChildrenParties(Set<Party> childrenParties) {
        this.childrenParties = childrenParties;
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

    public void setParentParty(Party parentParty) {
        this.parentParty = parentParty;
    }

    public void setParentSourceSystemRecordPk(String parentSourceSystemRecordPk) {
        this.parentSourceSystemRecordPk = parentSourceSystemRecordPk;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public void setPartyLevelTpCd(Long partyLevelTpCd) {
        this.partyLevelTpCd = partyLevelTpCd;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setSourceSystemId(Long sourceSystemId) {
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

    public Long getParentPartyId() {
        return parentPartyId;
    }

    public void setParentPartyId(Long parentPartyId) {
        this.parentPartyId = parentPartyId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {

        String party = "";

        if (childrenParties != null && !childrenParties.isEmpty())
            party = " \n Party [partyName=" + partyName + ", \n" + " childrenParties=[[[[" + childrenParties
                    + "]]]]]   ] \n";
        else
            party = " \n Party [partyName=" + partyName + ",  ]  \n";

        return party;

    }

}

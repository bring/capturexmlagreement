package no.bring.priceengine.dao;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "applicabilitycriteria", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ApplicabilityCriteriaEntity {


    @Id
    @SequenceGenerator(sequenceName = "core.applicabilitycriteria_applicabilitycriteria_id_seq", name = "ApplicabilityCriteriaIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ApplicabilityCriteriaIdSequence")
    @Column(name = "applicabilitycriteria_id")
    private Long applicabilityCriteriaId;

    @Column(name = "name")
    private String name;

/*	@Column(name = "description")
	private String description;*/

    @Column(name = "applicabilitycriteria")
    String applicabilityCriteriaJSONString;

    @Column(name = "status_tp_cd")
    private Integer statusTpCd;

    public Long getApplicabilityCriteriaId() {
        return applicabilityCriteriaId;
    }

    public void setApplicabilityCriteriaId(Long applicabilityCriteriaId) {
        this.applicabilityCriteriaId = applicabilityCriteriaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

/*	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}*/

    public String getApplicabilityCriteriaJSONString() {
        return applicabilityCriteriaJSONString;
    }

    public void setApplicabilityCriteriaJSONString(String applicabilityCriteriaJSONString) {
        this.applicabilityCriteriaJSONString = applicabilityCriteriaJSONString;
    }

    public Integer getStatusTpCd() {
        return statusTpCd;
    }

    public void setStatusTpCd(Integer statusTpCd) {
        this.statusTpCd = statusTpCd;
    }

}

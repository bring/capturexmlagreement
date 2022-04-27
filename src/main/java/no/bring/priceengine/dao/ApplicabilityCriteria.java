package no.bring.priceengine.dao;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ApplicabilityCriteria {

    CriteriaGroup criteriaGroup;

    public CriteriaGroup getCriteriaGroup() {
        return criteriaGroup;
    }

    public void setCriteriaGroup(CriteriaGroup criteriaGroup) {
        this.criteriaGroup = criteriaGroup;
    }

    @Override
    public String toString() {
        return "ApplicabilityCriteria [criteriaGroup=" + criteriaGroup + "]";
    }

}

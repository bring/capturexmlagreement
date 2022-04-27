package no.bring.priceengine.dao;

import org.hibernate.Criteria;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class CriteriaGroup {

    private List<Criteria> criterias;
    private List<CriteriaGroup> critGroups;
    String logicalOperator;

    @XmlElement(name = "Criteria")
    public List<Criteria> getCriterias() {
        return criterias;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setCriterias(List<Criteria> criterias) {
        this.criterias = criterias;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    @XmlElement(name = "CriteriaGroup")
    public List<CriteriaGroup> getCritGroups() {
        return critGroups;
    }

    public void setCritGroups(List<CriteriaGroup> critGroups) {
        this.critGroups = critGroups;
    }

    @Override
    public String toString() {
        return "CriteriaGroup [logicalOperator=" + logicalOperator + ", criterias=" + criterias + ", critGroups=" + critGroups + "]";
    }

}

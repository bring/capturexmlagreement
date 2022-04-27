package no.bring.priceengine.dao;

public class PriceAdjustment {

    private Long priceId;
    private Boolean isListPriceAdjustment;
    private Long priceAdjustmentTpCd;
    private String applicabilityCriteria;

    public String getApplicabilityCriteria() {
        return applicabilityCriteria;
    }

    public Boolean getIsListPriceAdjustment() {
        return isListPriceAdjustment;
    }

    public Long getPriceAdjustmentTpCd() {
        return priceAdjustmentTpCd;
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setApplicabilityCriteria(String applicabilityCriteria) {
        this.applicabilityCriteria = applicabilityCriteria;
    }

    public void setIsListPriceAdjustment(Boolean isListPriceAdjustment) {
        this.isListPriceAdjustment = isListPriceAdjustment;
    }

    public void setPriceAdjustmentTpCd(Long priceAdjustmentTpCd) {
        this.priceAdjustmentTpCd = priceAdjustmentTpCd;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

}

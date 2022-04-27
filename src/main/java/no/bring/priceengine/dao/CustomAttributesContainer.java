package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "CustomAttribute" })
public class CustomAttributesContainer {


    @JsonProperty("CustomAttribute")
    private Map<String, CustomAttribute> customAttributes = new HashMap<>();

    @JsonProperty("CustomAttribute")
    public Map<String, CustomAttribute> getCustomAttributes() {
        return customAttributes;
    }

    @JsonProperty("CustomAttribute")
    public void setCustomAttributes(Map<String, CustomAttribute> customAttributes) {
        this.customAttributes = customAttributes;
    }

    @Override
    public String toString() {
        return "CustomAttributesContainer [customAttributes=" + customAttributes + "]";
    }



    private static final String  VOL_WT_CONVERSION_FACTOR_NAME = "volWtConversionFactor";
    private static final String  PRICE_TAG_LEVEL_TP_CD_NAME = "priceTagLevelTpCd";



    public String getVolumetricFactorValue()
    {
        return getAttributeValue(VOL_WT_CONVERSION_FACTOR_NAME);
    }



    public String getPriceTagLevelTpCdValue()
    {
        return getAttributeValue(PRICE_TAG_LEVEL_TP_CD_NAME);
    }


    private String getAttributeValue(String attributeName)
    {
        String customAttributeValue  = null;

        if(customAttributes != null)
        {
            CustomAttribute ca = customAttributes.get(attributeName);
            if(ca != null)
                customAttributeValue = ca.getValue();
        }
        return customAttributeValue;
    }




}

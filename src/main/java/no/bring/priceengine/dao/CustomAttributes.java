package no.bring.priceengine.dao;

import org.codehaus.jettison.json.JSONObject;


public class CustomAttributes {

    private JSONObject volWtConversionFactor;


    public JSONObject getVolWtConversionFactor() {
        return volWtConversionFactor;
    }

    public void setVolWtConversionFactor(JSONObject volWtConversionFactor) {
        this.volWtConversionFactor = volWtConversionFactor;
    }
}

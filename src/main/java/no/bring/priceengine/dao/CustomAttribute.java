package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Name", "Value" })
public class CustomAttribute {


    @JsonProperty("Name")
    private String name;

    @JsonProperty("Value")
    private String value;

    public CustomAttribute() {
        super();

    }

    public CustomAttribute(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Value")
    public String getValue() {
        return value;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Value")
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CustomAttribute [name=" + name + ", value=" + value + "]";
    }

}

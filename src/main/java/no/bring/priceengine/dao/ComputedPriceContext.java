package no.bring.priceengine.dao;

public class ComputedPriceContext {
    private String containerType;
    private PostalCode fromPostalCode;
    private PostalCode toPostalCode;
    private  Item item;


    public String getContainerType() {
        return containerType;
    }

    public PostalCode getFromPostalCode() {
        return fromPostalCode;
    }

    public PostalCode getToPostalCode() {
        return toPostalCode;
    }



    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public void setFromPostalCode(PostalCode fromPostalCode) {
        this.fromPostalCode = fromPostalCode;
    }

    public void setToPostalCode(PostalCode toPostalCode) {
        this.toPostalCode = toPostalCode;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }


}

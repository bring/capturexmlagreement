package no.bring.priceengine.repository;

public class SFApplCriteria {


    public String serviceId;
    public String vasId;
    public String terminals;
    public String discountTypeRegion;
    public String direction;
    public String minimumPrice;
    public Boolean freePickUp;
    public String passiveReturn;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((direction == null) ? 0 : direction.hashCode());
        result = prime * result + ((discountTypeRegion == null) ? 0 : discountTypeRegion.hashCode());
        result = prime * result + ((freePickUp == null) ? 0 : freePickUp.hashCode());
        result = prime * result + ((minimumPrice == null) ? 0 : minimumPrice.hashCode());
        result = prime * result + ((passiveReturn == null) ? 0 : passiveReturn.hashCode());
        result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
        result = prime * result + ((terminals == null) ? 0 : terminals.hashCode());
        result = prime * result + ((vasId == null) ? 0 : vasId.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SFApplCriteria other = (SFApplCriteria) obj;
        if (direction == null) {
            if (other.direction != null)
                return false;
        } else if (!direction.equals(other.direction))
            return false;
        if (discountTypeRegion == null) {
            if (other.discountTypeRegion != null)
                return false;
        } else if (!discountTypeRegion.equals(other.discountTypeRegion))
            return false;
        if (freePickUp == null) {
            if (other.freePickUp != null)
                return false;
        } else if (!freePickUp.equals(other.freePickUp))
            return false;
        if (minimumPrice == null) {
            if (other.minimumPrice != null)
                return false;
        } else if (!minimumPrice.equals(other.minimumPrice))
            return false;
        if (passiveReturn == null) {
            if (other.passiveReturn != null)
                return false;
        } else if (!passiveReturn.equals(other.passiveReturn))
            return false;
        if (serviceId == null) {
            if (other.serviceId != null)
                return false;
        } else if (!serviceId.equals(other.serviceId))
            return false;
        if (terminals == null) {
            if (other.terminals != null)
                return false;
        } else if (!terminals.equals(other.terminals))
            return false;
        if (vasId == null) {
            if (other.vasId != null)
                return false;
        } else if (!vasId.equals(other.vasId))
            return false;

        return true;
    }

}

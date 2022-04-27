package no.bring.priceengine.dao;

import java.math.BigDecimal;

public class SlabPrice {

    private Long slabBasedPriceEntyId;

    private Integer priceBasisLowerBound;

    private Integer priceBasisUpperBound;

    private BigDecimal price;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SlabPrice other = (SlabPrice) obj;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        if (priceBasisLowerBound == null) {
            if (other.priceBasisLowerBound != null)
                return false;
        } else if (!priceBasisLowerBound.equals(other.priceBasisLowerBound))
            return false;
        if (priceBasisUpperBound == null) {
            if (other.priceBasisUpperBound != null)
                return false;
        } else if (!priceBasisUpperBound.equals(other.priceBasisUpperBound))
            return false;
        if (slabBasedPriceEntyId == null) {
            if (other.slabBasedPriceEntyId != null)
                return false;
        } else if (!slabBasedPriceEntyId.equals(other.slabBasedPriceEntyId))
            return false;
        return true;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getPriceBasisLowerBound() {
        return priceBasisLowerBound;
    }

    public Integer getPriceBasisUpperBound() {
        return priceBasisUpperBound;
    }

    public Long getSlabBasedPriceEntyId() {
        return slabBasedPriceEntyId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + ((priceBasisLowerBound == null) ? 0 : priceBasisLowerBound.hashCode());
        result = prime * result + ((priceBasisUpperBound == null) ? 0 : priceBasisUpperBound.hashCode());
        result = prime * result + ((slabBasedPriceEntyId == null) ? 0 : slabBasedPriceEntyId.hashCode());
        return result;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setPriceBasisLowerBound(Integer priceBasisLowerBound) {
        this.priceBasisLowerBound = priceBasisLowerBound;
    }

    public void setPriceBasisUpperBound(Integer priceBasisUpperBound) {
        this.priceBasisUpperBound = priceBasisUpperBound;
    }

    public void setSlabBasedPriceEntyId(Long slabBasedPriceEntyId) {
        this.slabBasedPriceEntyId = slabBasedPriceEntyId;
    }
}

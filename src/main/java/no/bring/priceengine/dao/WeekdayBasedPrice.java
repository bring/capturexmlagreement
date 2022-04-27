package no.bring.priceengine.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "weekdaybasedprice", schema = "core")
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class WeekdayBasedPrice  implements Serializable {


    @Column(name = "friday_price")
    private BigDecimal fridayPrice;

    @Column(name = "monday_price")
    private BigDecimal mondayPrice;

    @OneToOne
    @JoinColumn(name = "price_id")
    @JsonBackReference
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Price price;

    @Column(name = "saturday_price")
    private BigDecimal saturdayPrice;

    @Column(name = "sunday_price")
    private BigDecimal sundayPrice;

    @Column(name = "thursday_price")
    private BigDecimal thursdayPrice;

    @Column(name = "tuesday_price")
    private BigDecimal tuesdayPrice;

    @Column(name = "wednesday_price")
    private BigDecimal wednesdayPrice;

    @Id
    @Column(name = "weekdaybasedprice_Id")
    private Long weekdayBasedPriceId;

    protected WeekdayBasedPrice() {
    }

    public BigDecimal getFridayPrice() {
        return fridayPrice;
    }

    public BigDecimal getMondayPrice() {
        return mondayPrice;
    }

    public Price getPrice() {
        return price;
    }

    public BigDecimal getSaturdayPrice() {
        return saturdayPrice;
    }

    public BigDecimal getSundayPrice() {
        return sundayPrice;
    }

    public BigDecimal getThursdayPrice() {
        return thursdayPrice;
    }

    public BigDecimal getTuesdayPrice() {
        return tuesdayPrice;
    }

    public BigDecimal getWednesdayPrice() {
        return wednesdayPrice;
    }

    public Long getWeekdayBasedPriceId() {
        return weekdayBasedPriceId;
    }

    public void setFridayPrice(BigDecimal fridayPrice) {
        this.fridayPrice = fridayPrice;
    }

    public void setMondayPrice(BigDecimal mondayPrice) {
        this.mondayPrice = mondayPrice;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setSaturdayPrice(BigDecimal saturdayPrice) {
        this.saturdayPrice = saturdayPrice;
    }

    public void setSundayPrice(BigDecimal sundayPrice) {
        this.sundayPrice = sundayPrice;
    }

    public void setThursdayPrice(BigDecimal thursdayPrice) {
        this.thursdayPrice = thursdayPrice;
    }

    public void setTuesdayPrice(BigDecimal tuesdayPrice) {
        this.tuesdayPrice = tuesdayPrice;
    }

    public void setWednesdayPrice(BigDecimal wednesdayPrice) {
        this.wednesdayPrice = wednesdayPrice;
    }

    public void setWeekdayBasedPriceId(Long weekdayBasedPriceId) {
        this.weekdayBasedPriceId = weekdayBasedPriceId;
    }

}
